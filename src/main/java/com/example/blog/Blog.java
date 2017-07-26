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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.InputStream;
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
    private static Path blogFile = Paths.get("blog.txt");
    private static String blog = "";


    private void RestoreBlog()
    {
        try {
            InputStream in = Files.newInputStream(blogFile,StandardOpenOption.CREATE);
            blog = CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void SaveBlog()
    {
        try {
            OutputStream out = Files.newOutputStream(blogFile,StandardOpenOption.WRITE,StandardOpenOption.TRUNCATE_EXISTING);
            out.write(blog.getBytes());
            out.close();
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
