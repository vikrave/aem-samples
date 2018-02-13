package com.aem.samples.core.s3;

import java.io.InputStream;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.samples.core.DamUtil;
import com.aem.samples.core.SamplesHelper;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.workflow.event.WorkflowEvent;

//mark immediate=true else activation will be delayed
@Component(immediate = true, service = EventHandler.class, property = {
		"event.topics=" + ReplicationAction.EVENT_TOPIC })
public class S3UploadContentReplicationListener implements Runnable, EventHandler {

	private static final Logger LOG = LoggerFactory.getLogger(S3UploadContentReplicationListener.class);

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Override
	public void handleEvent(Event event) {

		// Create a system user for all the read/write operations and call it
		// here accordingly
		// Here the system user i have created has "writeService" as the
		// subservice name
		ResourceResolver resourceResolver = SamplesHelper.getResourceResolver(resolverFactory, "writeService");

		ReplicationAction action = ReplicationAction.fromEvent(event);

		if (action != null) {

			LOG.info("Replication action {} occured on {} ", action.getType().getName(), action.getPath());
			String path = action.getPath();

			LOG.debug("The asset activated is " + path);

			// Convert the path to resource
			Resource resource = resourceResolver.getResource(path);

			// It's obvious that the resource won't be empty in this case , but
			// incase of any unprecedented event , it's always good to have null
			// check
			if (resource != null) {
				InputStream stream = DamUtil.getAsset(resource);

				if (stream != null) {
					String name = DamUtil.getAssetName(resource);

					S3FileHandler s3FileHandler = new S3FileHandler();
					// Upload the file to the S3 server
					try {
						s3FileHandler.uploadFile(stream, name);
					} catch (Exception e) {
						LOG.error("Failed upload ", e);
					}

				}

			}
		}

	}

	public void run() {
		LOG.info("Running...");
	}

	protected void activate(ComponentContext ctx) {
		ctx.getBundleContext();
	}

	protected void deactivate(ComponentContext ctx) {
	}

}