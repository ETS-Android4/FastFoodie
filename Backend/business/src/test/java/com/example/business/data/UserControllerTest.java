package com.example.business.data;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.HttpStatus;

import com.example.business.data.controllers.UserController;
import com.example.business.data.entities.User;
import com.example.business.data.repositories.UserRepository;


/**
 * TODO, figure out how to best test with a DB
 * @author watis
 *
 */
public class UserControllerTest {
	
	@InjectMocks
	UserController userController;

	@Mock
	UserRepository repo;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getAccountByIdTest() {
		when(repo.findById("TomDodge@gmail.com")).thenReturn(Optional.of(new User("TomDodge@gmail.com", "registered")));
	
		Optional<User> uO = userController.getUser_OLD("TomDodge@gmail.com");
		
		User found = uO.get();
		assertEquals("TomDodge@gmail.com", found.getUser_email());
		assertEquals("registered", found.getUser_type());
	}
	
	@Test
	public void getAllAccountTest() {
		List<User> list = new ArrayList<User>();
		list.add(new User("TomDodge@gmail.com", "registered"));
		list.add(new User("wasartin@iastate.edu", "admin"));
		list.add(new User("BigBird@sesameStreet.com", "registered"));
		list.add(new User("TunaFey@rockCentralPlaza.com", "registered"));
		list.add(new User("hughMan@planetexpress.com", "registered"));
		list.add(new User("longjohnSilver@plentyOfFish.com", "registered"));
		list.add(new User("milkShakeSuma@dangerCart.com", "admin"));
		list.add(new User("vincentAdultman@corporatePlace.com", "admin"));
		list.add(new User("gilbertPatel@Uruk.com", "registered"));
		list.add(new User("cowman@Springs.com", "registered"));
		list.add(new User("grayman98@gmail.com", "admin"));
		list.add(new User("newunusedname@gmail.com", "admin"));
		list.add(new User("graysoncox98@gmail.com", "admin"));

		when(repo.findAll()).thenReturn(list);

		List<User> uList = (List<User>) userController.getAllUsers_OLD();

		assertEquals(13, uList.size());
		verify(repo, times(1)).findAll();
	}
	
	@Test
	public void createUserTest() {
		User found = new User("thisGuy@gmail.com", "admin");
		
		when(repo.save(found)).thenReturn(new User());

		JSONObject response = userController.createUser(found);
		assertThat(repo.save(found), is(notNullValue()));
		assertEquals(response.get("HttpStatus"), HttpStatus.OK);
		assertEquals(response.get("message"), "User has been created");
		assertEquals(response.get("status"), 204);
	}
	
	@Test
	public void createUserTest_fail() {
		User alreadyInDB = new User("TomDodge@gmail.com", "registered");
		
		when(repo.existsById("TomDodge@gmail.com")).thenReturn(true);
		when(repo.save(alreadyInDB)).thenReturn(new User());

		JSONObject response = userController.createUser(alreadyInDB);
		assertEquals(response.get("HttpStatus"), HttpStatus.BAD_REQUEST);
		assertEquals(response.get("message"), "User might already exist, or your fields are incorrect, double check your request");
		assertEquals(response.get("status"), 400);
	}
	
	@Test
	public void deleteUserTest() {
		//User toDelete = new User("thisGuy@gmail.com", "admin");
		//TODO
	}
	
	@Test
	public void editUserTest() {
		User userInDB = new User("TomDodge@gmail.com", "admin");
		
		when(repo.save(userInDB)).thenReturn(new User());
		when(repo.existsById("TomDodge@gmail.com")).thenReturn(true);

		JSONObject response = userController.editUser(userInDB, "TomDodge@gmail.com");

		assertThat(repo.save(userInDB), is(notNullValue()));
		System.out.println(response.toString());
		
		assertEquals(response.get("HttpStatus"), HttpStatus.OK);
		assertEquals(response.get("message"), "User has been edited");
		assertEquals(response.get("status"), 200);
	}
	
	@Test
	public void editUserTest_Fail() {
		User userInDB = new User("TomDodge@gmail.com", "admin");
		
		when(repo.save(userInDB)).thenReturn(new User());
		when(repo.existsById("TomDodge@gmail.com")).thenReturn(false);

		JSONObject response = userController.editUser(userInDB, "TomDodge@gmail.com");

		assertThat(repo.save(userInDB), is(notNullValue()));
		
		assertEquals(response.get("HttpStatus"), HttpStatus.BAD_REQUEST);
		assertEquals(response.get("message"), "Could not find that user in the database, or your fields are incorrect, double check your request");
		assertEquals(response.get("status"), 400);
	}

}
