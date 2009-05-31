/*
 * Copyright (c) 2006 Smart Bear Inc.  All Rights Reserved
 * Created on Feb 15, 2006 by smartbear.
 */
package com.smartbear.ccollab;

import com.smartbear.CollabClientException;
import com.smartbear.beans.ConfigUtils;
import com.smartbear.beans.GlobalOptions;
import com.smartbear.beans.ISettableGlobalOptions;
import com.smartbear.ccollab.client.CollabClientConnection;
import com.smartbear.ccollab.datamodel.ActionItem;
import com.smartbear.ccollab.datamodel.Assignment;
import com.smartbear.ccollab.datamodel.Changelist;
import com.smartbear.ccollab.datamodel.Comment;
import com.smartbear.ccollab.datamodel.Conversation;
import com.smartbear.ccollab.datamodel.ConversationSet;
import com.smartbear.ccollab.datamodel.Defect;
import com.smartbear.ccollab.datamodel.Engine;
import com.smartbear.ccollab.datamodel.Review;
import com.smartbear.ccollab.datamodel.Scm;
import com.smartbear.ccollab.datamodel.User;
import com.smartbear.ccollab.datamodel.Version;
import com.smartbear.scm.IScmAtomicChange;
import com.smartbear.scm.IScmClientConfiguration;
import com.smartbear.scm.IScmLocalCheckout;
import com.smartbear.scm.ScmChangeset;
import com.smartbear.scm.ScmUtils;
import com.smartbear.scm.impl.perforce.PerforceSystem;
import com.smartbear.scm.impl.subversion.SubversionSystem;
import org.eclipse.core.runtime.NullProgressMonitor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * A set of example sub-routines and full routines that do
 * various things with the Collaborator data model.
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public class Examples
{	
	
	/**
	 * Connection to the Collaborator server, created by init().
	 */
	private static CollabClientConnection client;
	
	/**
	 * Initialize the global connection to Collaborator.
	 */
	public static void init() throws CollabClientException, IOException
	{
		// If we've already initialized, don't do it again.
		if ( client != null ) {
			return;
		}
		
		//load options from config files
		ISettableGlobalOptions options = GlobalOptions.copy(ConfigUtils.loadConfigFiles());
		
		//initialize interface to client api
		client = new CollabClientConnection(new CommandLineClient(options),options);
	}
	
	/**
	 * Called to clean up a previous call to <code>init()</code>.
	 *
	 * <b>THIS IS CRITICAL</b>.  If you do not close out your <code>CollabClientConnection</code>
	 * object, data might not be flushed out to the server!
	 */
	public static void finished()
	{
		if (client != null) {
			client.finished(true, new NullProgressMonitor());
		}
	}

	/**
	 * Print some useful information about users, plus demonstrate
	 * how we can load specific user objects if we want to.
	 */
	public static void printUserInfo() throws CollabClientException, IOException
	{
		// Make sure we have all our global variables
		init();
		
		// Load the user object for the local user
		User localUser = client.getUser();
		if ( localUser == null )
		{
			System.out.println( "error: user not found: " + client.getOptions().getUser() );
			return;
		}
		
		// Display some information
		System.out.println( "local user: unique ID: " + localUser.getId() );
		System.out.println( "local user: login: " + localUser.getLogin() );
		System.out.println( "local user: name to display: " + localUser.getDisplayName() );
		System.out.println( "local user: email: " + localUser.getEmail() );
		System.out.println( "local user: lines of context for diff view: " + localUser.getNumLinesContext() );
	}
	
	/**
	 * Print information about a user's current action items
	 * @throws CollabClientException 
	 */
	public static void printUserActionItems() throws CollabClientException, IOException
	{
		// Make sure we have all our global variables
		init();
		
		// Load the user object for the local user
		User localUser = client.getUser();
		if ( localUser == null )
		{
			System.out.println( "error: user not found: " + client.getOptions().getUser() );
			return;
		}
		
		// Load the list of Action Items.
		// Actions Items are always returned in the order they should be displayed
		// to the end user.
		ActionItem[] items = localUser.getActionItems();
		
		// Display the list of Action Items.
		for ( int k = 0; k < items.length; k++ )
		{
			System.out.println( "===== ACTION ITEM #" + k + " ======" );
			System.out.println( "Text: " + items[k].getTitle() );
			System.out.println( "Related review (if any): " + ( items[k].getReview() == null ? "none" : items[k].getReview().getDisplayText( true ) ) );
			System.out.println( "Link (if any): " + ( items[k].getUrl() == null ? "none" : items[k].getUrl() ) );
		}
	}
	
	/**
	 * Print information about each of the reviews which this user
	 * has some kind of relationship.
	 * @throws CollabClientException 
	 */
	public static void printAssignments() throws CollabClientException, IOException
	{
		// Make sure we have all our global variables
		init();
		
		// Load the user object for the local user
		User localUser = client.getUser();
		if ( localUser == null )
		{
			System.out.println( "error: user not found: " + client.getOptions().getUser() );
			return;
		}
		
		// Load the first 100 assignments that name this user.
		Assignment[] assignments = client.getEngine(new NullProgressMonitor()).assignmentsFind( 100, null, localUser );
		
		// Display them.
		for ( int k = 0; k < assignments.length; k++ )
		{
			System.out.println( "===== " + assignments[k].getReview().getDisplayText( false ) + " ======" );
			System.out.println( "Review: " + assignments[k].getReview().getDisplayText( true ) );
			System.out.println( "Role of this user: " + assignments[k].getRole().getDisplayName() );
			System.out.println( "Is this user the author? " + ( assignments[k].getRole().isAuthor() ? "yes" : "no" ) );
		}
	}
	
	/**
	 * Print information about a particular review.
	 * 
	 * @param review the review to print information about
	 */
	public static void printReview( Review review ) throws CollabClientException, IOException
	{
		// Make sure we have all our global variables
		init();
		
		// Display basic review information
		System.out.println( "Review ID: " + review.getId() );
		System.out.println( "Review Title: " + review.getTitle() );
		System.out.println( "Review Creator: " + review.getCreator().getDisplayName() );
		System.out.println( "Review Created On: " + review.getCreationDate() );
		System.out.println( "Review Phase: " + review.getPhase().getName() );
		
		// Dump information about the participants
		Assignment[] assignments = review.getAssignments();
		for ( int a = 0; a < assignments.length; a++ )
		{
			System.out.println( "Assignment #" + a + ": User: " + assignments[a].getUser().getDisplayName() );
			System.out.println( "Assignment #" + a + ": Role: " + assignments[a].getRole().getDisplayName() );
		}
		
		// Dump information about the changelists, but only
		// get those changelists that are foremost in the review, not
		// changelists that were uploaded before but have been superceded
		// by new ones.  Use getChangelists() if you want all of them.
		Changelist[] changelists = review.getChangelistsActive( null );
		for ( int c = 0; c < changelists.length; c++ )
		{
			System.out.println( "Active Changelist #" + c + ": Collab ID: " + changelists[c].getId() );
			System.out.println( "Active Changelist #" + c + ": SCM ID: " + changelists[c].getScmIdentifier() );
			System.out.println( "Active Changelist #" + c + ": Date: " + changelists[c].getDate() );
			System.out.println( "Active Changelist #" + c + ": Author: " + changelists[c].getAuthor() );
			System.out.println( "Active Changelist #" + c + ": Comment: " + changelists[c].getComment() );
			
			// Local changes contain files on developer's machines, not yet checked
			// into version control or not controlled.  Otherwise the change is already
			// checked into version control.
			System.out.println( "Active Changelist #" + c + ": is local change: " + ( changelists[c].getLocalGuid().length() > 0 ) );
			
			// Dump information about files attached to this changelist
			Version[] versions = changelists[c].getVersions();
			for ( int v = 0; v < versions.length; v++ )
			{
				System.out.println( "Active Changelist #" + c + ": Version #" + v + ": path: " + versions[v].getFilePath() );
				System.out.println( "Active Changelist #" + c + ": Version #" + v + ": SCM ID: " + versions[v].getScmVersion() );
				System.out.println( "Active Changelist #" + c + ": Version #" + v + ": MD5 of content: " + versions[v].getContentMD5() );
				System.out.println( "Active Changelist #" + c + ": Version #" + v + ": bytes of content: " + versions[v].getContent().length );
			}
		}
		
		// Dump information about all conversations in this review,
		// threaded by changelist and version, with comments on old changelists
		// automatically promoted to new changelists.
		//
		// NOTE: YOU ALWAYS WANT TO PROMOTE!  It sometimes seems like you don't,
		//       but you probably do!
		ConversationSet conversationSet = review.getConversations( true );		// compute promoted conversations
		Conversation[] conversations = conversationSet.getConversations();		// access the entire list of conversations
		for ( int c = 0; c < conversations.length; c++ )
		{
			Conversation conversation = conversations[c];
			Version version = conversation.getVersion();
			System.out.println( "Conversation #" + c + ": Changelist ID: " + ( version == null ? "Whole Review" : version.getChangelist().getId().toString() ) );
			System.out.println( "Conversation #" + c + ": Version ID: " + ( version == null ? "Whole Review" : version.getId().toString() ) );
			System.out.println( "Conversation #" + c + ": Line: " + ( conversation.getLineNumber() <= 0 ? "Whole File" : conversation.getLineNumber().toString() ) );
			
			// Say if each of the users in this review will perceive this conversation as having "new" comments
			for (Assignment assignment : assignments) {
				boolean isNew = conversation.getFirstUnreadComment(assignment.getUser()) >= 0;
				System.out.println("Conversation #" + c + ": User " + assignment.getUser().getDisplayName() + " thinks is \"new\": " + isNew);
			}

			// Print the comments associated with this conversation
			Comment[] comments = conversation.getComments();
			for ( int k = 0; k < comments.length; k++ )
			{
				System.out.println( "Conversation #" + c + ": Comment #" + k + ": Date: " + comments[k].getCreationDate() );
				System.out.println( "Conversation #" + c + ": Comment #" + k + ": Author: " + comments[k].getCreator().getDisplayName() );
				System.out.println( "Conversation #" + c + ": Comment #" + k + ": Text: " + comments[k].getText() );
				System.out.println( "Conversation #" + c + ": Comment #" + k + ": Type: " + comments[k].getType().getCode() );
				System.out.println( "Conversation #" + c + ": Comment #" + k + ": Should display: " + comments[k].getType().isVisible() );
			}
			
			// Print the defects associated with this conversation
			Defect[] defects = conversation.getDefects();
			for ( int k = 0; k < defects.length; k++ )
			{
				System.out.println( "Conversation #" + c + ": Defect #" + k + ": Date: " + defects[k].getCreationDate() );
				System.out.println( "Conversation #" + c + ": Defect #" + k + ": Author: " + defects[k].getCreator().getDisplayName() );
				System.out.println( "Conversation #" + c + ": Defect #" + k + ": Text: " + defects[k].getText() );
			}
		}
	}
	
	/**
	 * Creates a new review object
	 */
	public static void createReview() throws CollabClientException, IOException
	{
		// Make sure we have all our global variables
		init();
		
		// Load the user object for the local user
		User localUser = client.getUser();
		if ( localUser == null )
		{
			System.out.println( "error: user not found: " + client.getOptions().getUser() );
			return;
		}
		
		// Create the new review object with the local user as the creator
		Review review = client.getEngine(new NullProgressMonitor()).reviewCreate( localUser, "Untitled Review" );
		review.setTitle( "Untitled Review" );
		review.save();	// when you change fields in objects, it's not really saved until you call save()
		System.out.println( "New review created: " + review.getDisplayText( true ) );
	}
	
	/**
	 * Attach a local file to a review in its own changeset
	 */
	public static void attachUncontrolledFile( Review review, String path ) throws CollabClientException, IOException
	{
		// Make sure we have all our global variables
		init();
		
		// Parameter validation
		if ( review == null )
		{
			System.err.println( "error: no such review" );
			return;
		}
		File file = new File( path );
		if ( ! file.exists() || ! file.isFile() )
		{
			System.err.println( "error: path not an existing file: " + file.getAbsolutePath() );
			return;
		}
		
		// Create the SCM object representing a local file NOT under version control.
		// We could create a bunch of these and attach them all if we wanted to.
		System.out.println( "Creating SCM File object..." );
		IScmLocalCheckout scmFile = ScmUtils.getUncontrolledFile( file );
		
		// Create the SCM ChangeSet object to upload.  You can attach
		// many types of objects here from uncontrolled files as in this
		// example to controlled files (both local and server-side-only)
		// to SCM-specific atomic changelists (e.g. with Perforce and Subversion).
		System.out.println( "Creating SCM Changeset..." );
		ScmChangeset changeset = new ScmChangeset();
		changeset.addLocalCheckout( scmFile, new NullProgressMonitor() );
		
		// Upload this changeset to Collaborator.  Another form of this
		// uploader lets us specific even more information; this form extracts it
		// automatically from the files in the changeset.
		System.out.println( "Uploading SCM Changeset..." );
		Scm scm = client.getEngine(new NullProgressMonitor()).scmNone(); // Use this when the files aren't under version control; otherwise we would be using scmConfiguredExternal().
		Changelist changelist = scm.uploadChangeset( changeset, "Uncontrolled Files", new NullProgressMonitor() );
		
		// The changelist has been uploaded but it hasn't been attached
		// to any particular review!  This two-step process not only allows for
		// a changelist to be part of more than one review, but also means that
		// if there's any error in uploading the changelist the review hasn't
		// changed at all so no one will be affected.
		review.addChangelist( changelist );
	}
	
	/**
	 * Attaches local files that are under version control to the given review
	 */
	public static void attachControlledFile( Review review, String path ) throws CollabClientException, IOException
	{
		// Make sure we have all our global variables
		init();
		
		// Parameter validation
		if ( review == null )
		{
			System.err.println( "error: no such review" );
			return;
		}
		File file = new File( path );
		if ( ! file.exists() || ! file.isFile() )
		{
			System.err.println( "error: path not an existing file: " + file.getAbsolutePath() );
			return;
		}
		
		// Create the SCM object representing a local file under version control.
		// We assume the local SCM is already configured properly.
		System.out.println( "Loading SCM File object..." );
		IScmClientConfiguration clientConfig = client.requireScm(null, new NullProgressMonitor(), ScmUtils.SCMS);
		IScmLocalCheckout scmFile = clientConfig.getLocalCheckout( file, new NullProgressMonitor() );
		
		// Create the SCM ChangeSet object to upload.  You can attach
		// many types of objects here from uncontrolled files as in this
		// example to controlled files (both local and server-side-only)
		// to SCM-specific atomic changelists (e.g. with Perforce and Subversion).
		System.out.println( "Creating SCM Changeset..." );
		ScmChangeset changeset = new ScmChangeset();
		changeset.addLocalCheckout( scmFile, new NullProgressMonitor() );
		
		// Upload this changeset to Collaborator.  Another form of this
		// uploader lets us specific even more information; this form extracts it
		// automatically from the files in the changeset.
		System.out.println( "Uploading SCM Changeset..." );
		Engine engine = client.getEngine(new NullProgressMonitor());
		Scm scm = engine.scmByLocalCheckout( clientConfig.getScmSystem(), scmFile );			// select the SCM system that matches the client configuration
		Changelist changelist = scm.uploadChangeset( changeset, "Local Files", new NullProgressMonitor() );
		
		// The changelist has been uploaded but it hasn't been attached
		// to any particular review!  This two-step process not only allows for
		// a changelist to be part of more than one review, but also means that
		// if there's any error in uploading the changelist the review hasn't
		// changed at all so no one will be affected.
		review.addChangelist( changelist );
	}
	
	/**
	 * Attach a changelist by ID (e.g. from Perforce or Subversion) to
	 * Code Collaborator, attaching to a given review.
	 */
	public static void attachChangelist( Review review, String changelistId ) throws CollabClientException, IOException
	{
		// Make sure we have all our global variables
		init();
		
		//get SCM connection to perforce or subversion
		IScmClientConfiguration clientConfig = client.requireScm(
				null, //get configuration from working directory
				new NullProgressMonitor(), 
				Arrays.asList(
						PerforceSystem.INSTANCE, SubversionSystem.INSTANCE)
				);
		
		// Parameter validation
		if ( review == null )
		{
			System.err.println( "error: no such review" );
			return;
		}
		
		// Load the SCM object representing the atomic changelist.
		// Leave with error if there's a problem.
		System.out.println( "Loading SCM changelist object..." );
		IScmAtomicChange scmChange = clientConfig.getChangelist( changelistId, new NullProgressMonitor() );
		if ( scmChange == null )
		{
			System.err.println( "error: either the changelist `" + changelistId + "` doesn't exist," );
			System.err.println( "       or " + clientConfig.getScmSystem().getName() + " doesn't support changelists." );
			return;
		}
		
		// Upload this atomic changelist to Collaborator.
		System.out.println( "Uploading SCM changelist to server..." );
		Engine engine = client.getEngine(new NullProgressMonitor());
		Scm scm = engine.scmByAtomicChange( clientConfig.getScmSystem(), clientConfig, scmChange );			// select the SCM system that matches the client configuration
		Changelist changelist = scm.uploadChangelist( scmChange, new NullProgressMonitor() );
		
		// The changelist has been uploaded but it hasn't been attached
		// to any particular review!  This two-step process not only allows for
		// a changelist to be part of more than one review, but also means that
		// if there's any error in uploading the changelist the review hasn't
		// changed at all so no one will be affected.
		review.addChangelist( changelist );
	}
	
	/**
	 * Utility for the main routine below to print how to use this example file.
	 */
	private static void printUsageStatement()
	{
		System.err.println( "Possible invocations of the command-line:" );
		System.err.println( "\tprintUserInfo" );
		System.err.println( "\tgetUserActionItems" );
		System.err.println( "\tprintAssignments" );
		System.err.println( "\tprintReview <review-id>" );
		System.err.println( "\tattachUncontrolledFile <review-id> <local-file>" );
		System.err.println( "\tattachControlledFile <review-id> <local-file>" );
		System.err.println( "\tattachChangelist <review-id> <changelist-id>" );
	}
	
	/**
	 * Routine that allows you to run these examples from a command-line
	 * @param argv first arg is which example to run
	 */
	public static void main( String[] argv )
	{
		try
		{
			// initialize the system
			init();
			
			Engine engine = client.getEngine(new NullProgressMonitor());
			
			if ( argv.length == 0 )
			{
				System.err.println( "ERROR: Must supply the name of an example to execute." );
				printUsageStatement();
			}
			else if ( argv[0].equals( "printUserInfo" ) ) {
				printUserInfo();
			}
			else if ( argv[0].equals( "getUserActionItems" ) ) {
				printUserActionItems();
			}
			else if ( argv[0].equals( "printAssignments" ) ) {
				printAssignments();
			}
			else if ( argv[0].equals( "printReview" ) ) {
				printReview(engine.reviewById(new Integer(argv[1])));
			}
			else if ( argv[0].equals( "createReview" ) ) {
				createReview();
			}
			else if ( argv[0].equals( "attachUncontrolledFile" ) ) {
				attachUncontrolledFile(engine.reviewById(new Integer(argv[1])), argv[2]);
			}
			else if ( argv[0].equals( "attachControlledFile" ) ) {
				attachControlledFile(engine.reviewById(new Integer(argv[1])), argv[2]);
			}
			else if ( argv[0].equals( "attachChangelist" ) ) {
				attachChangelist(engine.reviewById(new Integer(argv[1])), argv[2]);
			}
			else 
			{
				System.err.println( "`" + argv[0] + "` is not an example." );
				printUsageStatement();
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			// This is critical -- you must close out your Client Connection api object or else
			// things you think you've "saved" might not actually be flushed to the server.
			finished();
		}
	}
}
