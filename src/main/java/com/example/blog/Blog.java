package com.example.blog;
import com.google.api.client.util.Charsets;
import com.google.api.server.spi.auth.EspAuthenticator;
import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiIssuer;
import com.google.api.server.spi.config.ApiIssuerAudience;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.common.io.CharStreams;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

// [START blog_api_annotation]
@Api(
        name = "blog",
        version = "v1",
        namespace =
        @ApiNamespace(
                ownerDomain = "blog.example.com",
                ownerName = "blog.example.com",
                packagePath = ""
        ),
        // [START_EXCLUDE]
        issuers = {
                @ApiIssuer(
                        name = "firebase",
                        issuer = "https://securetoken.google.com/csis-604-blog",
                        jwksUri = "https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com")
        }
        // [END_EXCLUDE]
)
// [END blog_api_annotation]
public class Blog
{
    private static String blogBucketName="csis-604-blog";
    private static String blogObjectName="blog";
    private static String blog = "";

    private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
            .initialRetryDelayMillis(10)
            .retryMaxAttempts(10)
            .totalRetryPeriodMillis(15000)
            .build());

    /**Used below to determine the size of chucks to read in. Should be > 1kb and < 10MB */
    private static final int BUFFER_SIZE = 2 * 1024 * 1024;

    private void copy(InputStream input, OutputStream output) throws IOException {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = input.read(buffer);
            while (bytesRead != -1) {
                output.write(buffer, 0, bytesRead);
                bytesRead = input.read(buffer);
            }
        } finally {
            input.close();
            output.close();
        }
    }
    private void RestoreBlog()
    {
        try {
            GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
            GcsFilename fileName = new GcsFilename(blogBucketName,blogObjectName);
            GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(fileName, 0, BUFFER_SIZE);
            ByteArrayOutputStream blogStream = new ByteArrayOutputStream();
            copy(Channels.newInputStream(readChannel), blogStream);
            blog = blogStream.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void SaveBlog()
    {
        try {
            GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
            GcsFilename fileName = new GcsFilename(blogBucketName,blogObjectName);
            GcsOutputChannel outputChannel;
            outputChannel = gcsService.createOrReplace(fileName, instance);
            InputStream blogStream = new ByteArrayInputStream(blog.getBytes());
            copy(blogStream, Channels.newOutputStream(outputChannel));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Blog()
    {
        RestoreBlog();
    }

    // [START getBlog_method]
    @ApiMethod(
            httpMethod = ApiMethod.HttpMethod.GET,
            name = "getBlog",
            path = "getBlog")
    public BlogEntry getBlog() 
	{
        BlogEntry blogEntry = new BlogEntry();
        blogEntry.setBlogEntry(blog);

        return blogEntry;
    }
    // [END getBlog_method]
	
    // [START editBlog_method]
    @ApiMethod(
            httpMethod = ApiMethod.HttpMethod.POST,
            name = "editBlog",
            path = "editBlog")
    public void editBlog(BlogEntry blogEntry) 
	{
		blog += '\n'+blogEntry.getBlogEntry();

		SaveBlog();
   	}
    // [END editBlog_method]
	
    // [START newBlog_method]
    @ApiMethod(
            httpMethod = ApiMethod.HttpMethod.PUT,
            name = "newBlog",
            path = "newBlog")
    public void newBlog(BlogEntry blogEntry) 
	{
		blog = blogEntry.getBlogEntry();

		SaveBlog();
   	}
    // [END newBlog_method]
	
    // [START deleteBlog_method]
    @ApiMethod(
            httpMethod = ApiMethod.HttpMethod.DELETE,
            name = "deleteBlog",
            path = "deleteBlog")
    public void deleteBlog() 
	{
		blog = "";

		SaveBlog();
   	}
    // [END deleteBlog_method]
}
