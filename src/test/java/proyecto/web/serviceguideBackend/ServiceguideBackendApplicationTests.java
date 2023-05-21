package proyecto.web.serviceguideBackend;

import org.checkerframework.checker.nullness.Opt;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import proyecto.web.serviceguideBackend.config.UserAuthenticationProvider;
import proyecto.web.serviceguideBackend.dto.*;
import proyecto.web.serviceguideBackend.entities.*;
import proyecto.web.serviceguideBackend.repositories.UserRepository;
import proyecto.web.serviceguideBackend.serviceInterface.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
class ServiceguideBackendApplicationTests {

	@Autowired
	private AuthInterface authInterface;

	@Autowired
	private CityInterface cityInterface;

	@Autowired
	private HouseInterface houseInterface;

	@Autowired
	private ReceiptInterface receiptInterface;

	@Autowired
	private StatisticInterface statisticInterface;

	@Autowired
	private UserInterface userInterface;

	@Autowired
	private UserAuthenticationProvider userAuthenticationProvider;

	@Autowired
	private UserRepository userRepository;


	@Test
	void contextLoads() {
	}

	@Test
	void testAuthFindByEmail() {
		String email = "serviceguide23@gmail.com";

		UserDto userDto = authInterface.findByEmail(email);

		Assertions.assertNotNull(userDto);
		Assertions.assertEquals(email, userDto.getEmail());
	}

	@Test
	void testCityListAll() {
		Collection<City> cityList = cityInterface.listAll();

		Assertions.assertNotNull(cityList);
	}

	@Test
	void testCityFindById() {
		Long id = 1L;

		Optional<City> optionalCity = cityInterface.findById(id);
		Assertions.assertNotNull(optionalCity);
	}

	@Test
	void testCityFindByCity() {
		String city = "Medellín";

		Optional<City> optionalCity = cityInterface.findByCity(city);
		Assertions.assertNotNull(optionalCity);
		Assertions.assertEquals(city, optionalCity.get().getCity());
	}

	@Test
	void testStatisticIndividualReceipt() {
		Long idReceipt = 46L;
		String typeReceipt = "ENERGY";
		String typeGraphic = "Bar";

		StatisticDto newStatistic = statisticInterface.individualReceipt(typeReceipt, idReceipt, typeGraphic);

		Assertions.assertNotNull(newStatistic);
	}

	@Test
	void testStatisticGetStatisticByReceipt() {
		Long id = 45L;
		List<Statistic> statistics = statisticInterface.getStatisticByReceipt(id);

		Assertions.assertNotNull(statistics);
	}

	@Test
	void testUserLogin() {

		CredentialsDto credentialsDto = new CredentialsDto("serviceguide23@gmail.com", "1234".toCharArray());

		UserDto userDto = userInterface.login(credentialsDto);

		Assertions.assertNotNull(userDto);
		Assertions.assertEquals("serviceguide23@gmail.com", userDto.getEmail());
		Assertions.assertEquals(2L, userDto.getId());
	}
	/*
	@Test
	void testUserRegister() {
		SignUpDto signUpDto = new SignUpDto();
		signUpDto.setFirstName("Prueba");
		signUpDto.setLastName("Test");
		signUpDto.setEmail("prueba@gmail.com");
		signUpDto.setPassword("prueba123".toCharArray());

		UserDto registeredUser = userInterface.register(signUpDto);

		Assertions.assertNotNull(registeredUser.getId());
		Assertions.assertEquals(signUpDto.getEmail(), registeredUser.getEmail());
		Assertions.assertEquals(signUpDto.getFirstName(), registeredUser.getFirstName());
		Assertions.assertEquals(signUpDto.getLastName(), registeredUser.getLastName());
	}*/

	@Test
	void testUserGetByEmail() {
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

		CredentialsDto credentialsDto = new CredentialsDto("serviceguide23@gmail.com", "1234".toCharArray());
		UserDto userDto = userInterface.login(credentialsDto);
		userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

		String token = userDto.getToken();

		Optional<User> optionalUser = userInterface.findById(token);

		Assertions.assertTrue(optionalUser.isPresent());

		User user = optionalUser.orElse(null);
		Assertions.assertNotNull(user);

		Assertions.assertEquals("serviceguide23@gmail.com", user.getEmail());
		Assertions.assertEquals("Service", user.getFirstName());
		Assertions.assertEquals("Guide", user.getLastName());
	}

	@Test
	void testUserUpdateUser() {
		CredentialsDto credentialsDto = new CredentialsDto("prueba4@gmail.com", "prueba123".toCharArray());
		UserDto userDto = userInterface.login(credentialsDto);
		userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));
		String token = userDto.getToken();

		SignUpDto updateUserDto = new SignUpDto();
		updateUserDto.setFirstName("Prueba Update");
		updateUserDto.setLastName("Test Update");
		updateUserDto.setEmail("prueba5@gmail.com");
		updateUserDto.setPassword("prueba123".toCharArray());

		Optional<UpdateUserDto> updatedUserDto = userInterface.updateUser(updateUserDto, token);
		Assertions.assertEquals("User updated", updatedUserDto.get().getMessage());

		User updatedUser = userRepository.findByEmail(updateUserDto.getEmail()).orElse(null);

		Assertions.assertEquals(updateUserDto.getFirstName(), updatedUser.getFirstName());
		Assertions.assertEquals(updateUserDto.getLastName(), updatedUser.getLastName());
	}

	@Test
	void testUserListAll() {
		Collection<User> userList = userInterface.listAll();

		Assertions.assertNotNull(userList);
	}
	/*
	@Test
	void testUserFindByTokenPassword() {
		CredentialsDto credentialsDto = new CredentialsDto("serviceguide23@gmail.com", "1234".toCharArray());
		UserDto userDto = userInterface.login(credentialsDto);
		String tokenPassword = userDto.getToken();


		Optional<User> optionalUser = userInterface.findByTokenPassword(tokenPassword);

		Assertions.assertTrue(optionalUser.isPresent());

		User user = optionalUser.orElse(null);
		Assertions.assertNotNull(user);

		Assertions.assertEquals("serviceguide23@gmail.com", user.getEmail());
		Assertions.assertEquals("Service", user.getFirstName());
		Assertions.assertEquals("Guide", user.getLastName());
	}*/
	/*
	@Test
	void testUserSave() {
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

	@Test
	void testUserDelete() {
		CredentialsDto credentialsDto = new CredentialsDto("prueba5@gmail.com", "prueba123".toCharArray());
		UserDto userDto = userInterface.login(credentialsDto);
		userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));
		String token = userDto.getToken();

		Message deleteMessage = userInterface.delete(token);

		Assertions.assertNotNull(deleteMessage);
		Assertions.assertEquals("Delete success", deleteMessage.getMessage());
	}

}
