package com.aem.samples.core;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.dam.api.Rendition;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.replication.Replicator;

public class DamUtil {

	private static final Logger LOG = LoggerFactory.getLogger(DamUtil.class);
	 
    
    
    /**
	 * Method will return input stream of the asset
	 * 
	 * @param resource
	 * @return {@link InputStream}
	 */
	public static InputStream getAsset(Resource resource) {
		Asset asset = resource.adaptTo(Asset.class);

		InputStream stream = null;
		// Check if valid asset object
		if (asset != null) {
			Rendition original = asset.getOriginal();

			// See if the asset has original rendition
			// Else the asset might be corrupted
			if (original != null) {
				stream = original.getStream();
			}
		}

		return stream;
	}

	
	/**
	 * Method will return the mimetype of the resource/asset
	 * @param resource
	 * @return {@link String}
	 */
	public static String getMimeType(Resource resource) {
		Asset asset = resource.adaptTo(Asset.class);

		String mimeType = "";
		// Check if valid asset object
		if (asset != null) {
			Rendition original = asset.getOriginal();

			// See if the asset has original rendition
			// Else the asset might be corrupted
			if (original != null) {
				mimeType = original.getMimeType();
			}
		}

		return mimeType;
	}

	/**
	 * Method will return the name of the asset
	 * 
	 * @param resource
	 * @return
	 */
	public static String getAssetName(Resource resource) {
		Asset asset = resource.adaptTo(Asset.class);

		String name = "";
		// Check if valid asset object
		if (asset != null) {
			name = asset.getName();
		}

		return name;
	}
}
