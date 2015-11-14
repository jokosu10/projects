package com.example.aris.spark_test;

import org.junit.Test;

import junit.framework.Assert;

@SuppressWarnings("deprecation")
public class HelloWorldTest {
	
	@Test
	public void runTest() {
		Assert.assertEquals(HelloWorld.printHello(), "Hello World");
	}

}
