package com.aem.samples.core.utils.impl;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.samples.core.utils.services.SampleServiceOsgi;


//immediate - Defines whether the component is to be instantiated immediately (true) or on-demand (false).
//Mark your service immediate=true , to make it active as soon as the bundle is active 
//Otherwise your service will be in satisfied state only


//OSGI annotations don't use @Service like in Apache Felix SCR annotations
//We need to mention the service type as a property.

@Component(immediate = true, service = SampleServiceOsgi.class)
public class SampleServiceOsgiImpl implements SampleServiceOsgi {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	@Override
	public void sampleMethod(){
		LOG.info("This is a sample service implementation using OSGI declarative service annotations");
	}
}
