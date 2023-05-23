package proyecto.web.serviceguideBackend;

import org.checkerframework.checker.nullness.Opt;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import proyecto.web.serviceguideBackend.config.UserAuthenticationProvider;
import proyecto.web.serviceguideBackend.dto.*;
import proyecto.web.serviceguideBackend.entities.*;
import proyecto.web.serviceguideBackend.repositories.HouseRepository;
import proyecto.web.serviceguideBackend.repositories.UserRepository;
import proyecto.web.serviceguideBackend.serviceInterface.*;

import java.util.*;

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

	//TESTS AUTHENTICATION
	//-------------------------------------------------------------------------------------
	@Test
	void testAuthFindByEmail() {
		String email = "serviceguide23@gmail.com";

		UserDto userDto = authInterface.findByEmail(email);

		Assertions.assertNotNull(userDto);
		Assertions.assertEquals(email, userDto.getEmail());
	}

	//TESTS CITY
	//-------------------------------------------------------------------------------------
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

	//TESTS HOUSE
	//-------------------------------------------------------------------------------------
	@Test
	void testHouseNewHouse() {
		CredentialsDto credentialsDto = new CredentialsDto("serviceguide23@gmail.com", "1234".toCharArray());
		UserDto userDto = userInterface.login(credentialsDto);
		userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

		String token = userDto.getToken();

		Optional<User> optionalUser = userInterface.findById(token);
		User user = optionalUser.orElseThrow(() -> new RuntimeException("User not found"));

		City city = new City();
		city.setId(1L);
		city.setCity("Medellín");

		HouseDto houseDto = new HouseDto();
		houseDto.setName("Prueba Test");
		houseDto.setStratum(6);
		houseDto.setNeighborhood("La Castellana");
		houseDto.setAddress("Carrera 34 AA");
		houseDto.setContract("123456");
		houseDto.setCities(city);
		houseDto.setUser(user);

		HouseDto createdHouseDto = houseInterface.newHouse(houseDto, token);

		Assertions.assertNotNull(createdHouseDto);
		Assertions.assertEquals(houseDto.getName(), createdHouseDto.getName());
		Assertions.assertEquals(houseDto.getStratum(), createdHouseDto.getStratum());
		Assertions.assertEquals(houseDto.getNeighborhood(), createdHouseDto.getNeighborhood());
		Assertions.assertEquals(houseDto.getAddress(), createdHouseDto.getAddress());
		Assertions.assertEquals(houseDto.getContract(), createdHouseDto.getContract());
	}

	@Test
	void testHouseFindAllByUserOrderById() {
		CredentialsDto credentialsDto = new CredentialsDto("serviceguide23@gmail.com", "1234".toCharArray());
		UserDto userDto = userInterface.login(credentialsDto);
		userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

		String token = userDto.getToken();

		Collection<House> houseCollection = houseInterface.findAllByUserOrderById(token);

		Assertions.assertNotNull(houseCollection);
	}

	@Test
	void testHouseFindByUserAndName() {
		CredentialsDto credentialsDto = new CredentialsDto("serviceguide23@gmail.com", "1234".toCharArray());
		UserDto userDto = userInterface.login(credentialsDto);
		userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

		String token = userDto.getToken();
		String name = "Prueba Test";

		Optional<House> optionalHouse = houseInterface.findByUserAndName(token, name);

		Assertions.assertNotNull(optionalHouse);
		Assertions.assertEquals(name, optionalHouse.get().getName());
	}

	@Test
	void testHouseFindById() {
		CredentialsDto credentialsDto = new CredentialsDto("serviceguide23@gmail.com", "1234".toCharArray());
		UserDto userDto = userInterface.login(credentialsDto);
		userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

		Long id = 14L;
		String name = "Prueba Test";

		Optional<House> optionalHouse = houseInterface.findById(id);

		Assertions.assertNotNull(optionalHouse);
		Assertions.assertEquals(id, optionalHouse.get().getId());
		Assertions.assertEquals(name, optionalHouse.get().getName());
	}

	@Test
	void testHouseGetHouseName() {
		CredentialsDto credentialsDto = new CredentialsDto("serviceguide23@gmail.com", "1234".toCharArray());
		UserDto userDto = userInterface.login(credentialsDto);
		userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

		String token = userDto.getToken();

		Collection<String> houseCollection = houseInterface.getHouseName(token);
		String name = "Prueba Test";
		boolean found = false;

		for (String houseName : houseCollection) {
			if (houseName.equals(name)) {
				found = true;
				break;
			}
		}

		Assertions.assertNotNull(houseCollection);
		Assertions.assertTrue(found, "El nombre '" + name + "' no se encuentra en la colección de nombres de casas.");
	}

	@Test
	void testHouseUpdateHouse() {
		CredentialsDto credentialsDto = new CredentialsDto("serviceguide23@gmail.com", "1234".toCharArray());
		UserDto userDto = userInterface.login(credentialsDto);
		userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

		City city = new City();
		city.setId(1L);
		city.setCity("Medellín");

		Long id = 14L;
		HouseDto updateHouse = new HouseDto();
		updateHouse.setName("Casa Prueba");
		updateHouse.setStratum(6);
		updateHouse.setNeighborhood("La Castellana");
		updateHouse.setContract("123456");
		updateHouse.setCities(city);

		Optional<Message> updateResult = houseInterface.updateHouse(updateHouse, id);

		Assertions.assertTrue(updateResult.isPresent(), "La actualización de la casa falló");
		Message message = updateResult.get();
		Assertions.assertEquals("House Updated successfully", message.getMessage(), "El mensaje de actualización de la casa es incorrecto");
		Assertions.assertEquals(HttpStatus.OK, message.getStatus(), "El estado de la respuesta de actualización de la casa es incorrecto");

	}

	@Test
	void testHouseDeleteHouse() {
		CredentialsDto credentialsDto = new CredentialsDto("serviceguide23@gmail.com", "1234".toCharArray());
		UserDto userDto = userInterface.login(credentialsDto);
		userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

		Long id = 15L;

		Message deleteMessage = houseInterface.deleteHouse(id);


		Assertions.assertNotNull(deleteMessage);
		Assertions.assertEquals("Delete success", deleteMessage.getMessage());
	}

	//TESTS RECEIPTS
	//-------------------------------------------------------------------------------------
	@Test
	void testReceiptsNewReceipt() {
		CredentialsDto credentialsDto = new CredentialsDto("serviceguide23@gmail.com", "1234".toCharArray());
		UserDto userDto = userInterface.login(credentialsDto);
		userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

		String token = userDto.getToken();

		Calendar calendar = Calendar.getInstance();
		calendar.set(2023, Calendar.FEBRUARY, 10);
		Date date = calendar.getTime();

		TypeService typeService = new TypeService();
		typeService.setType("WATER");

		String houseName = "Casa Laureles";
		Optional<House> optionalHouse = houseInterface.findByUserAndName(token, houseName);
		House house = optionalHouse.orElseThrow(() -> new RuntimeException("House not found"));

		ReceiptDto receiptDto = new ReceiptDto();
		receiptDto.setReceiptName("Recibo Prueba Test 2");
		receiptDto.setPrice(20000D);
		receiptDto.setAmount(100D);
		receiptDto.setDate(date);
		receiptDto.setTypeService(typeService);
		receiptDto.setHouse(house);

		ReceiptDto createdReceiptDto = receiptInterface.newReceipt(receiptDto, token);

		Assertions.assertNotNull(createdReceiptDto);
		Assertions.assertEquals(receiptDto.getReceiptName(), createdReceiptDto.getReceiptName());
		Assertions.assertEquals(receiptDto.getAmount(), createdReceiptDto.getAmount());
		Assertions.assertEquals(receiptDto.getPrice(), createdReceiptDto.getPrice());
	}

	@Test
	void testReceiptFindByHouse() {
		CredentialsDto credentialsDto = new CredentialsDto("serviceguide23@gmail.com", "1234".toCharArray());
		UserDto userDto = userInterface.login(credentialsDto);
		userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

		String token = userDto.getToken();
		String houseName = "Casa Laureles";

		Collection<Receipt> receiptCollection = receiptInterface.findByHouse(houseName, token);

		boolean found = false;

		for (Receipt houseNames : receiptCollection) {
			if (houseNames.getHouseName() != null && houseNames.getHouseName().equals(houseName)) {
				found = true;
				break;
			}
		}

		Assertions.assertNotNull(receiptCollection);
		Assertions.assertTrue(found, "El nombre '" + houseName + "' no se encuentra en la colección de nombres de recibos.");
	}

	@Test
	void testReceiptsFindByTypeServiceAndHouse() {
		CredentialsDto credentialsDto = new CredentialsDto("serviceguide23@gmail.com", "1234".toCharArray());
		UserDto userDto = userInterface.login(credentialsDto);
		userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

		String token = userDto.getToken();
		String typeService = "WATER";
		String houseName = "Casa Laureles";
		String receiptName = "Recibo Prueba Test";

		Collection<Receipt> receiptCollection = receiptInterface.findByTypeServiceAndHouse(typeService, houseName, token);

		boolean found = false;

		for (Receipt receipts : receiptCollection) {
			if (receipts.getReceiptName() != null && receipts.getReceiptName().equals(receiptName)){
				found = true;
				break;
			}
		}

		Assertions.assertNotNull(receiptCollection);
		Assertions.assertTrue(found, "El '" + receiptName + "' no se encuentra en la colección de recibos");
	}

	@Test
	void testReceiptsAllReceiptsByUserId() {
		CredentialsDto credentialsDto = new CredentialsDto("serviceguide23@gmail.com", "1234".toCharArray());
		UserDto userDto = userInterface.login(credentialsDto);
		userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

		String token = userDto.getToken();

		List<Receipt> receiptList = receiptInterface.allReceiptsByUserId(token);

		Assertions.assertNotNull(receiptList);
	}

	@Test
	void testReceiptsFindById() {
		CredentialsDto credentialsDto = new CredentialsDto("serviceguide23@gmail.com", "1234".toCharArray());
		UserDto userDto = userInterface.login(credentialsDto);
		userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

		Long id = 63L;
		String receiptName = "Recibo Prueba Test 2";
		String houseName = "Casa Laureles";

		Optional<Receipt> optionalReceipt = receiptInterface.findById(id);

		Assertions.assertEquals(receiptName, optionalReceipt.get().getReceiptName());
		Assertions.assertEquals(houseName, optionalReceipt.get().getHouseName());
	}

	@Test
	void testReceiptsGetLastReceipt() {
		CredentialsDto credentialsDto = new CredentialsDto("serviceguide23@gmail.com", "1234".toCharArray());
		UserDto userDto = userInterface.login(credentialsDto);
		userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

		String token = userDto.getToken();
		String lastReceiptName = "Recibo Prueba Test 2";

		Optional<Receipt> optionalReceipt = receiptInterface.getLastReceipt(token);

		Assertions.assertNotNull(optionalReceipt);
		Assertions.assertEquals(lastReceiptName, optionalReceipt.get().getReceiptName());
	}
	/*
	@Test
	void testReceiptUpdateReceipt() {

	}*/

	@Test
	void testReceiptgetAllReceiptsByType() {
		CredentialsDto credentialsDto = new CredentialsDto("serviceguide23@gmail.com", "1234".toCharArray());
		UserDto userDto = userInterface.login(credentialsDto);
		userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

		String token = userDto.getToken();
		String type = "WATER";
		String receiptName = "Recibo Prueba";
		Double receiptPrice = 30000D;

		Collection<Receipt> receiptCollection = receiptInterface.getAllReceiptsByType(token, type);

		boolean found = false;

		for (Receipt receipts : receiptCollection) {
			if (receipts != null && receipts.getReceiptName().equals(receiptName) && receipts.getPrice().equals(receiptPrice)) {
				found = true;
				break;
			}
		}

		Assertions.assertNotNull(receiptCollection);
		Assertions.assertTrue(found, "El '" + receiptName + "' con precio '" + receiptPrice + "' no se encuentra en la colección de recibos");
	}

	@Test
	void testReceiptGetTwoReceiptById() {
		CredentialsDto credentialsDto = new CredentialsDto("serviceguide23@gmail.com", "1234".toCharArray());
		UserDto userDto = userInterface.login(credentialsDto);
		userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

		Long idReceipt = 63L;

		Long receiptId = receiptInterface.getTwoReceiptById(idReceipt);

		Assertions.assertNotNull(receiptId);
	}

	@Test
	void testReceiptDeleteReceipt() {
		CredentialsDto credentialsDto = new CredentialsDto("serviceguide23@gmail.com", "1234".toCharArray());
		UserDto userDto = userInterface.login(credentialsDto);
		userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

		Long id = 62L;

		Message deletedReceipt = receiptInterface.deleteReceipt(id);

		Assertions.assertNotNull(deletedReceipt);
		Assertions.assertEquals("Received deleted successfully", deletedReceipt.getMessage());
	}

	//TESTS STATISTIC
	//-------------------------------------------------------------------------------------
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

	//TESTS USER
	//-------------------------------------------------------------------------------------
	@Test
	void testUserLogin() {

		CredentialsDto credentialsDto = new CredentialsDto("serviceguide23@gmail.com", "1234".toCharArray());

		UserDto userDto = userInterface.login(credentialsDto);

		Assertions.assertNotNull(userDto);
		Assertions.assertEquals("serviceguide23@gmail.com", userDto.getEmail());
		Assertions.assertEquals(2L, userDto.getId());
	}

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
	}

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
		CredentialsDto credentialsDto = new CredentialsDto("prueba@gmail.com", "prueba123".toCharArray());
		UserDto userDto = userInterface.login(credentialsDto);
		userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));
		String token = userDto.getToken();

		Message deleteMessage = userInterface.delete(token);

		Assertions.assertNotNull(deleteMessage);
		Assertions.assertEquals("Delete success", deleteMessage.getMessage());
	}

}
