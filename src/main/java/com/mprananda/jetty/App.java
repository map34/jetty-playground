package com.mprananda.jetty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;

/**
 * Hello world!
 *
 */
public class App 
{
    private Server server;
    private static final int PORT = 8080;
    private static Logger logger = LogManager.getLogger(App.class);

    private void start() throws Exception {
        server = new Server(createThreadPool());
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(PORT);
        server.setConnectors(new Connector[] {connector});

        server.setHandler(getWebAppContext());
        server.start();
        server.dump(System.out);
        server.join();
    }

    private QueuedThreadPool createThreadPool() {
        int maxThreads = 100;
        int minThreads = 10;
        int idleTimeout = 120;

        return new QueuedThreadPool(maxThreads, minThreads, idleTimeout);
    }

    private WebAppContext getWebAppContext() throws MalformedURLException {
        URI webResourceBase = findWebResourceBase(server.getClass().getClassLoader());
        logger.info("Using BaseResource: {}", webResourceBase);
        WebAppContext context = new WebAppContext();
        context.setBaseResource(Resource.newResource(webResourceBase));
        context.setConfigurations(new Configuration[]
                {
                        new AnnotationConfiguration(),
                        new WebInfConfiguration(),
                        new WebXmlConfiguration(),
                        new MetaInfConfiguration(),
                        new FragmentConfiguration(),
                        new EnvConfiguration(),
                        new PlusConfiguration(),
                        new JettyWebXmlConfiguration()
                });

        context.setContextPath("/");
        context.setParentLoaderPriority(true);
        return context;
    }

    private static URI findWebResourceBase(ClassLoader classLoader)
    {
        String webResourceRef = "WEB-INF/web.xml";

        try
        {
            // Look for resource in classpath (best choice when working with archive jar/war file)
            URL webXml = classLoader.getResource('/' + webResourceRef);
            if (webXml != null)
            {
                URI uri = webXml.toURI().resolve("..").normalize();
                logger.info("WebResourceBase (Using ClassLoader reference) {}", uri);
                return uri;
            }
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException("Bad ClassPath reference for: " + webResourceRef,e);
        }

        // Look for resource in common file system paths
        try
        {
            Path pwd = new File(System.getProperty("user.dir")).toPath().toRealPath();
            FileSystem fs = pwd.getFileSystem();

            // Try the generated maven path first
            PathMatcher matcher = fs.getPathMatcher("glob:**/jetty-playground-*");
            try (DirectoryStream<Path> dir = Files.newDirectoryStream(pwd.resolve("target")))
            {
                for(Path path: dir)
                {
                    if(Files.isDirectory(path) && matcher.matches(path))
                    {
                        // Found a potential directory
                        Path possible = path.resolve(webResourceRef);
                        // Does it have what we need?
                        if(Files.exists(possible))
                        {
                            URI uri = path.toUri();
                            logger.info("WebResourceBase (Using discovered /target/ Path) {}", uri);
                            return uri;
                        }
                    }
                }
            }

            // Try the source path next
            Path srcWebapp = pwd.resolve("src/main/webapp/" + webResourceRef);
            if(Files.exists(srcWebapp))
            {
                URI uri = srcWebapp.getParent().toUri();
                logger.info("WebResourceBase (Using /src/main/webapp/ Path) {}", uri);
                return uri;
            }
        }
        catch (Throwable t)
        {
            throw new RuntimeException("Unable to find web resource in file system: " + webResourceRef, t);
        }

        throw new RuntimeException("Unable to find web resource ref: " + webResourceRef);
    }

    public static void main( String[] args )
    {
        App app = new App();
        try {
            app.start();
        } catch (Exception ex) {
            logger.error(ex);
        }
    }
}
