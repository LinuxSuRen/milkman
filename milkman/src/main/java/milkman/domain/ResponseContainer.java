package milkman.domain;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(include = As.PROPERTY, use = Id.CLASS)
public abstract class ResponseContainer {

	private List<ResponseAspect> aspects = new LinkedList<ResponseAspect>();

	
	@JsonIgnore
	private CompletableFuture<Map<String, String>> statusInformations = new CompletableFuture<>();
	
	
	public <T extends ResponseAspect> Optional<T> getAspect(Class<T> aspectType) {
		return aspects.stream()
				.filter(aspectType::isInstance)
				.findAny()
				.map(a -> (T)a);
	}
}
