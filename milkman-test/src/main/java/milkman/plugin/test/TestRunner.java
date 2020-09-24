package milkman.plugin.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milkman.domain.RequestContainer;
import milkman.domain.ResponseContainer;
import milkman.plugin.test.domain.TestAspect;
import milkman.plugin.test.domain.TestContainer;
import milkman.plugin.test.domain.TestResultAspect;
import milkman.plugin.test.domain.TestResultAspect.TestResultEvent;
import milkman.plugin.test.domain.TestResultContainer;
import milkman.ui.plugin.PluginRequestExecutor;
import milkman.ui.plugin.Templater;
import milkman.utils.AsyncResponseControl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.scheduler.Schedulers;

import static milkman.plugin.test.domain.TestResultAspect.TestResultState.*;

@Slf4j
@RequiredArgsConstructor
public class TestRunner {

	private final PluginRequestExecutor executor;

	public ResponseContainer executeRequest(TestContainer request,
											Templater templater,
											AsyncResponseControl.AsyncControl asyncControl) {

		var testAspect = request.getAspect(TestAspect.class)
				.orElseThrow(() -> new IllegalArgumentException("Missing test aspect"));

		asyncControl.triggerReqeuestStarted();


		Flux<TestResultEvent> replay = ReplayProcessor.create(sink -> {
			Flux.fromIterable(testAspect.getRequests())
					.flatMap(reqId -> Mono.justOrEmpty(executor.getDetails(reqId)))
					.doOnNext(requestContainer -> sink.next(new TestResultEvent(requestContainer.getId(), requestContainer.getName(), STARTED)))
					.flatMap(requestContainer -> execute(requestContainer, sink))
//				.switchIfEmpty(Mono.defer(() -> {
//					log.error("Request could not be found");
//					return Mono.just(new TestResultEvent("", "", TestResultAspect.TestResultState.EXCEPTION));
//				}))
					.doOnComplete(() -> {
						asyncControl.triggerRequestSucceeded();
						sink.complete();
					})
					.subscribeOn(Schedulers.parallel())
					.publish().connect();
		});

		var container = new TestResultContainer();
		container.getAspects().add(new TestResultAspect(replay));
		return container;
	}

	private Mono<Void> execute(RequestContainer request, FluxSink<TestResultEvent> replay) {
		return Mono.defer(() -> Mono.just(executor.executeRequest(request)))
				.map(res -> Mono.fromFuture(res.getStatusInformations()))
				.doOnNext(si -> replay.next(new TestResultEvent(request.getId(), request.getName(), SUCCEEDED)))
				.doOnError(t -> replay.next(new TestResultEvent(request.getId(), request.getName(), FAILED)))
				.then();
	}
}
