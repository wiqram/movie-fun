package org.superbiz.moviefun;

import org.junit.Test;
import org.superbiz.moviefun.blobstore.ServiceCredentials;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class ServiceCredentialsTest {

    @Test
    public void testGetCredential() throws Exception {
        String json =
            "{\"user-provided\": [" +
                "  {" +
                "    \"name\": \"photo-storage\"," +
                "    \"instance_name\": \"photo-storage\"," +
                "    \"binding_name\": null," +
                "    \"credentials\": {" +
                "      \"access_key_id\": \"AOG4VEUKIAIAJFM6FHIA\"," +
                "      \"secret_access_key\": \"hCte2uMZiAqV74oxdwfGHNd+3G0C11hEe2HFh4w4\"," +
                "      \"bucket\": \"cf-813f2ksj-b28a-4427-baab-822f11s25f30\"," +
                "      \"endpoint\": \"www.example.com\"" +
                "    }," +
                "    \"syslog_drain_url\": \"\"," +
                "    \"volume_mounts\": []," +
                "    \"label\": \"user-provided\"," +
                "    \"tags\": []" +
                "  }" +
                "]}";

        ServiceCredentials serviceCredentials = new ServiceCredentials(json);

        String s3AccessKey = serviceCredentials.getCredential("photo-storage", "user-provided", "access_key_id");
        String s3SecretKey = serviceCredentials.getCredential("photo-storage", "user-provided", "secret_access_key");
        String s3BucketName = serviceCredentials.getCredential("photo-storage", "user-provided", "bucket");
        String endpoint = serviceCredentials.getCredential("photo-storage", "user-provided", "endpoint");

        assertThat(s3AccessKey, equalTo("AOG4VEUKIAIAJFM6FHIA"));
        assertThat(s3SecretKey, equalTo("hCte2uMZiAqV74oxdwfGHNd+3G0C11hEe2HFh4w4"));
        assertThat(s3BucketName, equalTo("cf-813f2ksj-b28a-4427-baab-822f11s25f30"));
        assertThat(endpoint, equalTo("www.example.com"));
    }
}
