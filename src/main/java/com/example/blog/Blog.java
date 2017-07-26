package com.example.blog;
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
    private static String blog = "this is an initial blog entry";

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
   	}
    // [END deleteBlog_method]
}
