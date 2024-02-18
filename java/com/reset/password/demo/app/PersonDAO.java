package com.reset.password.demo.app;


public interface PersonDAO {

	public Person  login(Person person);

	public Person  signup(Person person);

	public boolean updatePassword(String email, String password);

	public Person findByEmail(String email);

}
