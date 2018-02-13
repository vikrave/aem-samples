package com.aem.samples.core.s3;

import java.io.InputStream;
import java.nio.file.Path;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.samples.core.s3.constants.S3Constants;
import com.aem.samples.core.utils.services.OsgiConfigurationService;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3FileHandler {

	private static final String SUFFIX = "/";

	private static final Logger LOG = LoggerFactory.getLogger(S3FileHandler.class);

	// This is the configuration node from where we will be pulling the s3
	// configurations for the bucket
	// This will be present under the /apps/<project>/config.<mode>
	private static final String CONFIG_PID = "com.aem.samples.core.s3.BucketConfigurations";

	/**
	 * Uploads the {@link InputStream} with {@link String} fileName
	 * 
	 * @param fileInputStream
	 * @param fileName
	 * @return {@link String} fileName if successfully uploaded
	 */
	public String uploadFile(InputStream fileInputStream, String fileName) {

		OsgiConfigurationService configService = getOsgiConfigService();

		AmazonS3 s3client = null;
		Path path = null;
		try {

			// upload file to folder and set it to public
			String filePath = configService.getProperty(CONFIG_PID, S3Constants.S3_FOLDER_PATH, new String()) + SUFFIX
					+ fileName;

			// credentials object identifying user for authentication
			// user must have AWSConnector and AmazonS3FullAccess for
			AWSCredentials credentials = new BasicAWSCredentials(
					configService.getProperty(CONFIG_PID, S3Constants.S3_ACCESS_KEY, new String()),
					configService.getProperty(CONFIG_PID, S3Constants.S3_ACCESS_PASSWORD, new String()));

			// create a client connection based on credentials
			try {
				s3client = AmazonS3ClientBuilder.standard()
						.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.US_EAST_1)
						.build();

			} catch (Exception e) {
				LOG.error("FAILED ***", e);
			}

			s3client.putObject(new PutObjectRequest(
					configService.getProperty(CONFIG_PID, S3Constants.S3_BUCKET_NAME, new String()), filePath,
					fileInputStream, null).withCannedAcl(CannedAccessControlList.PublicRead));

			return fileName;

		} catch (Exception e) {
			LOG.error("FAILED ***", e);
		} finally {
			if (s3client != null) {
				s3client = null;
			}
			if (path != null) {
				path = null;
			}
		}

		return null;
	}

	/**
	 * This will return a service object for {@link OsgiConfigurationService}
	 * @return {@link OsgiConfigurationService}
	 */
	public OsgiConfigurationService getOsgiConfigService() {
		BundleContext bundleContext = FrameworkUtil.getBundle(OsgiConfigurationService.class).getBundleContext();
		ServiceReference<?> factoryRef = bundleContext.getServiceReference(OsgiConfigurationService.class.getName());
		OsgiConfigurationService configService = (OsgiConfigurationService) bundleContext.getService(factoryRef);
		return configService;
	}

}
