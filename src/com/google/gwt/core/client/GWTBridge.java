package com.google.gwt.core.client;
/**
* 
* Weird bug fix for gwt bug 7527 (see https://code.google.com/p/google-web-toolkit/issues/detail?id=7527)
* that causes a java.lang.ClassNotFoundException: com.google.gwt.core.client.GWTBridge error
* when app is deployed. Should be fixed when GWT 2.5.1 is released, but we'll see. 
* 
* This code taken from page: http://alexluca.com/2013/01/17/gwt-25-and-extgwt-224-classnotfoundexception-comgooglegwtcoreclientgwtbridge/
* This class is used for fixing class not found com.google.gwt.core.client.GWTBridge;
*
* This is only needed for GXT 2 to work. Once we remove GXT2, this can be removed also.
*/
public abstract class GWTBridge extends com.google.gwt.core.shared.GWTBridge {
}
