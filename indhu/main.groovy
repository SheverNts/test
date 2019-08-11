import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import groovy.json.JsonSlurper

import org.apache.commons.codec.binary.Base64;
			String webPage = "http://192.168.43.238:7990/rest/api/1.0/projects/HALO/repos";
			String name = "shever";
			String password = "redhat";

			String authString = name + ":" + password;
			System.out.println("auth string: " + authString);
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);
			System.out.println("Base64 encoded auth string: " + authStringEnc);

			URL url = new URL(webPage);
			URLConnection urlConnection = url.openConnection();
			urlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
//            println (urlConnection.getInputStream())

def output = urlConnection.getInputStream().getText();

def data = new JsonSlurper().parseText(output)
def repo_list = data.values

for ( repo in repo_list ) {
println ("utfvrefyjr")
println (repo.name)
}
