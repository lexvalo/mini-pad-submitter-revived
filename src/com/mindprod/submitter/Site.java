package com.mindprod.submitter;

import com.mindprod.http.Get;
import com.mindprod.http.Post;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A submission target, loaded at runtime from an external, human-editable
 * {@code sites.txt} file instead of being hardcoded as a Java enum. Add,
 * remove, or edit entries in {@code sites.txt} - no recompilation needed,
 * just restart the program.
 */
final class Site
    {
    private static final int CONNECT_TIMEOUT = ( int ) TimeUnit.SECONDS.toMillis( 40 );
    private static final int READ_TIMEOUT = ( int ) TimeUnit.SECONDS.toMillis( 20 );
    private static final String PAD_PLACEHOLDER = "$PAD";

    private static int responseCode;
    private static String responseMessage;

    private final String name;
    private final URL homeUrl;
    private final String action;
    private final String method;
    private final List<String[]> queryParams;
    private final List<String[]> bodyParams;

    private Site( String name, String homeUrl, String action, String method,
                  List<String[]> queryParams, List<String[]> bodyParams ) throws MalformedURLException
        {
        this.name = name;
        this.homeUrl = new URL( homeUrl );
        this.action = action;
        this.method = method;
        this.queryParams = queryParams;
        this.bodyParams = bodyParams;
        }

    String getName()
        {
        return name;
        }

    static int getResponseCode()
        {
        return responseCode;
        }

    static String getResponseMessage()
        {
        return responseMessage;
        }

    private URL getActionURL() throws MalformedURLException
        {
        // override tail end with action, either absolute or relative, same as the original SubmissionSite.
        return new URL( homeUrl, action );
        }

    private static String[] fillIn( List<String[]> pairs, String pad )
        {
        final String[] flat = new String[ pairs.size() * 2 ];
        int i = 0;
        for ( String[] pair : pairs )
            {
            flat[ i++ ] = pair[ 0 ];
            flat[ i++ ] = PAD_PLACEHOLDER.equals( pair[ 1 ] ) ? pad : pair[ 1 ];
            }
        return flat;
        }

    /**
     * Simulate manual submit, exactly like the original SubmissionSite enum did,
     * just driven by data loaded from sites.txt instead of hardcoded per-site Java.
     *
     * @param pad URL of the pad xml file we are submitting.
     */
    String submit( String pad )
        {
        try
            {
            final URL actionURL = getActionURL();
            final String result;
            if ( "GET".equals( method ) )
                {
                final Get get = new Get();
                get.setReadTimeout( READ_TIMEOUT );
                get.setParms( fillIn( bodyParams, pad ) );
                result = get.send( actionURL.getHost(), -1 /* port */, actionURL.getPath(), Get.UTF8 );
                responseCode = get.getResponseCode();
                responseMessage = get.getResponseMessage();
                }
            else
                {
                final Post post = new Post();
                post.setConnectTimeout( CONNECT_TIMEOUT );
                post.setReadTimeout( READ_TIMEOUT );
                if ( !queryParams.isEmpty() )
                    {
                    // rare case (e.g. RecoveryReview): both query-string and POST-body parms.
                    post.setParms( fillIn( queryParams, pad ) );
                    }
                post.setPostParms( fillIn( bodyParams, pad ) );
                result = post.send( actionURL.getHost(), -1 /* port */, actionURL.getPath(), Post.UTF8 );
                responseCode = post.getResponseCode();
                responseMessage = post.getResponseMessage();
                }
            return result;
            }
        catch ( MalformedURLException e )
            {
            responseCode = -1;
            responseMessage = "Bad URL for " + name + ": " + e.getMessage();
            return null;
            }
        }

    /**
     * Load every site from sites.txt - looked for next to the running jar first,
     * then in the current directory.
     */
    static List<Site> loadAll()
        {
        final File file = findSitesFile();
        if ( file == null )
            {
            throw new IllegalStateException(
                    "Could not find sites.txt next to submitter-patched.jar or in the current directory." );
            }
        final List<Site> sites = new ArrayList<>();
        try ( BufferedReader reader = new BufferedReader( new FileReader( file ) ) )
            {
            String name = null;
            String homeUrl = null;
            String action = null;
            String method = null;
            List<String[]> queryParams = new ArrayList<>();
            List<String[]> bodyParams = new ArrayList<>();
            String line;
            while ( ( line = reader.readLine() ) != null )
                {
                final String trimmed = line.trim();
                if ( trimmed.isEmpty() )
                    {
                    if ( name != null )
                        {
                        sites.add( new Site( name, homeUrl, action, method, queryParams, bodyParams ) );
                        }
                    name = null;
                    queryParams = new ArrayList<>();
                    bodyParams = new ArrayList<>();
                    continue;
                    }
                if ( trimmed.startsWith( "#" ) )
                    {
                    continue;
                    }
                if ( name == null )
                    {
                    final String[] header = trimmed.split( "\\|", -1 );
                    name = header[ 0 ];
                    homeUrl = header[ 1 ];
                    action = header[ 2 ];
                    method = header[ 3 ];
                    }
                else
                    {
                    String keyValue = trimmed;
                    final List<String[]> target;
                    if ( keyValue.startsWith( "query:" ) )
                        {
                        keyValue = keyValue.substring( "query:".length() );
                        target = queryParams;
                        }
                    else
                        {
                        target = bodyParams;
                        }
                    final int eq = keyValue.indexOf( '=' );
                    target.add( new String[] { keyValue.substring( 0, eq ), keyValue.substring( eq + 1 ) } );
                    }
                }
            if ( name != null )
                {
                sites.add( new Site( name, homeUrl, action, method, queryParams, bodyParams ) );
                }
            }
        catch ( IOException e )
            {
            throw new IllegalStateException( "Could not read " + file + ": " + e.getMessage(), e );
            }
        return Collections.unmodifiableList( sites );
        }

    private static File findSitesFile()
        {
        try
            {
            final File jarFile = new File( Site.class.getProtectionDomain().getCodeSource().getLocation().toURI() );
            final File next = new File( jarFile.getParentFile(), "sites.txt" );
            if ( next.isFile() )
                {
                return next;
                }
            }
        catch ( URISyntaxException | NullPointerException e )
            {
            // fall through to current directory
            }
        final File cwd = new File( "sites.txt" );
        return cwd.isFile() ? cwd : null;
        }
    }
