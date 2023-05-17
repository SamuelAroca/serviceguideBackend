package proyecto.web.serviceguideBackend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import proyecto.web.serviceguideBackend.dto.CredentialsDto;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.dto.UserDto;
import proyecto.web.serviceguideBackend.entities.City;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.serviceInterface.UserInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
class ServiceguideBackendApplicationTests {

	@Autowired
	private UserInterface userInterface;

	@Test
	void contextLoads() {
	}

	/*@Test
	void testLogin() {

		CredentialsDto credentialsDto = new CredentialsDto("serviceguide23@gmail.com", "$2a$10$MGvIKtsdX77nusYtHbV.pudS4ib0kHnlDo.1Nvoc0kHXQg2MWuH4y".toCharArray());

		UserDto userDto = userInterface.login(credentialsDto);

		Assertions.assertNotNull(userDto);
		Assertions.assertEquals("serviceguide23@gmail.com", userDto.getEmail());
		Assertions.assertEquals(2L, userDto.getId());
	}*/

	@Test
	void testListAll() {
		Collection<User> userList = userInterface.listAll();

		Assertions.assertNotNull(userList);
	}

	@Test
	void testGetByEmail() {
		String email = "serviceguide23@gmail.com";

		Optional<User> optionalUser = userInterface.getByEmail(email);

		Assertions.assertTrue(optionalUser.isPresent());

		User user = optionalUser.orElse(null);
		Assertions.assertNotNull(user);

		Assertions.assertEquals(email, optionalUser.get().getEmail());
		Assertions.assertEquals("Service", optionalUser.get().getFirstName());
		Assertions.assertEquals("Guide", optionalUser.get().getLastName());
	}

	@Test
	void testFindById() {

		CredentialsDto credentialsDto = new CredentialsDto("serviceguide23@gmail.com", "$2a$10$MGvIKtsdX77nusYtHbV.pudS4ib0kHnlDo.1Nvoc0kHXQg2MWuH4y".toCharArray());

		String token = userInterface.login(credentialsDto).getToken();

		Optional<User> optionalUser = userInterface.findById(token);

		Assertions.assertTrue(optionalUser.isPresent());

		User user = optionalUser.orElse(null);
		Assertions.assertNotNull(user);

		Assertions.assertEquals("serviceguide23@gmail.com", user.getEmail());
		Assertions.assertEquals("Service", user.getFirstName());
		Assertions.assertEquals("Guide", user.getLastName());

	}

	/*@Test
	void testSave() {
		User user = new User();
		user.setFirstName("Javier");
		user.setLastName("Cordoba");
		user.setEmail("javier@gamil.com");
		user.setPassword("1234");
		String userId = String.valueOf(user.getId());

		List<House> houses = new ArrayList<>();
		City city = new City(1L, "Medellin");

		House house1 = new House();
		house1.setContract("1234");
		house1.setAddress("Calle 22");
		house1.setName("Casa 1");
		house1.setStratum(6);
		house1.setNeighborhood("La Castellana");
		house1.setCities(city);
		house1.setUser(user);

		House house2 = new House();
		house2.setContract("1234");
		house2.setAddress("Calle 22");
		house2.setName("Casa 2");
		house2.setStratum(2);
		house2.setNeighborhood("Laureles");
		house2.setCities(city);
		house2.setUser(user);

		houses.add(house1);
		houses.add(house2);

		user.setHouse(houses);

		userInterface.save(user);

		User savedUser = userInterface.findById(userId).orElse(null);
		Assertions.assertNotNull(savedUser);
		Assertions.assertEquals(2, savedUser.getHouse().size());
	}*/

	/*@Test
	void testDelete() {
		String token = "user_token";

		Message deleteMessage = userInterface.delete(token);

		Assertions.assertNotNull(deleteMessage);
		Assertions.assertEquals("User deleted", deleteMessage.getMessage());
	}*/

}
