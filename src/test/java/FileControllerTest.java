import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netology.diplom.CloudStoreApplication;
import com.netology.diplom.auth.AuthRequest;
import com.netology.diplom.auth.Login;
import com.netology.diplom.exceptions.ApiErrorResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

@SpringBootTest(classes = CloudStoreApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileControllerTest {

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	private static Login login;

	@Order(1)
	@Test
	void testLoginWithUnknownUser() throws Exception {
		final AuthRequest loginForm = new AuthRequest();
		loginForm.setLogin("user1");
		loginForm.setPassword("password");
		String responseAsString = mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginForm))).andExpect(status().isNotFound())
				.andReturn()
				.getResponse()
				.getContentAsString();
		ApiErrorResponse errorResponse = objectMapper.readValue(responseAsString, ApiErrorResponse.class);
		Assertions.assertEquals("User not found", errorResponse.getMessage());
	}
	@Order(2)
	@Test
	void testLoginWithKnownUser() throws Exception {
		final AuthRequest loginForm = new AuthRequest();
		loginForm.setLogin("user1");
		loginForm.setPassword("password");
		final AuthRequest registerForm = new AuthRequest();
		registerForm.setLogin("user1");
		registerForm.setPassword("password");
		mockMvc.perform(post("/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(registerForm))).andExpect(status().isOk())
				.andReturn();
		String responseAsString = mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginForm))).andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
		login = objectMapper.readValue(responseAsString, Login.class);
	}
		@Order(3)
		@Test
		void testUploadFile() throws Exception {

		final String file = ".\\target\\test-classes\\images\\avatar.png";
		final byte[] multiPart = fileToFBytes(file);
		final MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "avatar.png", MediaType.IMAGE_PNG.getType(), multiPart);
		final MvcResult result = mockMvc.perform(((MockMultipartHttpServletRequestBuilder)
						MockMvcRequestBuilders.multipart("/file").header("auth-token", login.getAuthToken()))
		.file(mockMultipartFile).contentType(MediaType.MULTIPART_FORM_DATA))
				.andExpect(status().isCreated()).andReturn();
		final String response = result.getResponse().getContentAsString();
		assertThat(response).isNotNull();

	}
	private  byte[] fileToFBytes(final String path) throws IOException {
		final java.io.File file = new java.io.File(path);
		return FileUtils.readFileToByteArray(file);
	}
}



