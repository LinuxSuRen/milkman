package milkman.ui.plugin.rest.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import milkman.domain.ResponseAspect;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DebugRequestBodyAspect implements ResponseAspect {

	String body;

	@Override
	public String getName() {
		return "debugBody";
	}
	
}
