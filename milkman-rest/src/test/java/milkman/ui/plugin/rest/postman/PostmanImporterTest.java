package milkman.ui.plugin.rest.postman;


import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;


import milkman.domain.Collection;
import milkman.domain.Environment;
import milkman.domain.Folder;
import milkman.ui.plugin.rest.postman.importers.PostmanImporterV10;
import milkman.ui.plugin.rest.postman.importers.PostmanImporterV21;

class PostmanImporterTest {

	@Test
	void shouldConvertCollectionProperly() throws IOException, Exception {
		PostmanImporterV21 sut = new PostmanImporterV21();
		String json = IOUtils.toString(getClass().getResourceAsStream("/test.postman_collection.json"));
		Collection collection  = sut.importCollection(json);
		assertThat(collection.getName()).isEqualTo("test");
	}
	
	@Test
	void shouldImportNestedFoldersCorrectly() throws IOException, Exception {
		PostmanImporterV21 sut = new PostmanImporterV21();
		String json = IOUtils.toString(getClass().getResourceAsStream("/nested_test.postman_collection.json"));
		Collection collection  = sut.importCollection(json);
		assertThat(collection.getName()).isEqualTo("new");
		assertThat(collection.getFolders()).extracting(Folder::getName).containsExactly("lvl1");
		assertThat(collection.getFolders().get(0).getFolders()).extracting(Folder::getName).containsExactly("lvl2");
	}

	@Test
	void shouldConvertEnvironmentProperly() throws IOException, Exception {
		PostmanImporterV21 sut = new PostmanImporterV21();
		String json = IOUtils.toString(getClass().getResourceAsStream("/test.postman_environment.json"));

		Environment env = sut.importEnvironment(json);
		assertThat(env.getName()).isEqualTo("test");
	}
	
	@Test
	void shouldConvertStringUrlsProperly() throws IOException, Exception {
		PostmanImporterV21 sut = new PostmanImporterV21();
		String json = IOUtils.toString(getClass().getResourceAsStream("/testStringUrl.postman_collection.json"));

		Collection collection  = sut.importCollection(json);
		assertThat(collection.getName()).isEqualTo("Service");
	}
	

	@Test
	void shouldImportV10Collection() throws IOException, Exception {
		PostmanImporterV10 sut = new PostmanImporterV10();
		String json = IOUtils.toString(getClass().getResourceAsStream("/test.postman_collectionv10.json"));

		Collection env = sut.importCollection(json);
		assertThat(env.getName()).isEqualTo("TestServiceCall");
	}
}
