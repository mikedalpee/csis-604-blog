# CSIS 604 Web Service Assignment

This project uses the Google Cloud Endpoints Framework to simulate a very simple 
blog entry backend.  It uses the HTTP GET, PUT, POST, and DELETE commands
to manipulate the blog data. The data is persistently stored in the cloud 
using a GCS bucket and object.  When the service is started, it retrieves
the saved blog text. Each update causes the current blog text to be saved.


## Build with Maven

### Building the  project

To build the project:

    mvn clean package

### Generating the openapi.json file

To generate the required configuration file `openapi.json`:

    mvn exec:java -DGetSwaggerDoc

### Deploying the project to App Engine

To deploy the project:

0. Invoke the `gcloud` command to deploy the API configuration file:

         gcloud service-management deploy openapi.json

0. Deploy the API implementation code by invoking:

         mvn appengine:deploy

    The first time you upload a sample app, you may be prompted to authorize the
    deployment. Follow the prompts: when you are presented with a browser window
    containing a code, copy it to the terminal window.

0. Wait for the upload to finish.

### Using `curl` to sent interact with the blog

After you deploy the API and its configuration file, you can send requests
to the API.

#### Retrieve the current blog entry

     curl \
         -H "Content-Type: application/json" \
         -X GET \
         https://csis-604-blog.appspot.com/_ah/api/blog/v1/getBlog

You will get a 200 response with the following data:

    {
     "blockEntry": "<the current blog text>"
    }

#### Replace the current blog entry with new text

     curl \
         -H "Content-Type: application/json" \
         -X PUT \
         -d '{"blogEntry":"a new blog entry"}' \
         https://csis-604-blog.appspot.com/_ah/api/blog/v1/newBlog

     curl \
         -H "Content-Type: application/json" \
         -X GET \
         https://csis-604-blog.appspot.com/_ah/api/blog/v1/getBlog

After executing both commands you will get a 200 response with the following data:

    {
     "blockEntry": "a new blog entry"
    }

#### Delete the current blog entry

     curl \
         -H "Content-Type: application/json" \
         -X DELETE \
         https://csis-604-blog.appspot.com/_ah/api/blog/v1/deleteBlog

     curl \
         -H "Content-Type: application/json" \
         -X GET \
         https://csis-604-blog.appspot.com/_ah/api/blog/v1/getBlog

After executing both commands you will get a 200 response with the following data:

    {
     "blockEntry": ""
    }

#### Edit the current blog entry by appending a new line of text

     curl \
         -H "Content-Type: application/json" \
         -X POST \
         -d '{"blogEntry":"an edited blog entry"}' \
         https://csis-604-blog.appspot.com/_ah/api/blog/v1/editBlog

     curl \
         -H "Content-Type: application/json" \
         -X GET \
         https://csis-604-blog.appspot.com/_ah/api/blog/v1/getBlog

After executing both commands you will get a 200 response with the following data:

    {
     "blockEntry": "<existing blog text>\nan edited blog entry"
    }
