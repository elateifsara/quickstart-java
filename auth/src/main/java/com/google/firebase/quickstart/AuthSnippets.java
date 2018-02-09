package com.google.firebase.quickstart;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.ExportedUserRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.ListUsersPage;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Auth snippets for documentation.
 *
 * See:
 * https://firebase.google.com/docs/auth/admin
 */
public class AuthSnippets {

  public static void getUserById(String uid) throws InterruptedException, ExecutionException {
    // [START get_user_by_id]
    UserRecord userRecord = FirebaseAuth.getInstance().getUserAsync(uid).get();
    // See the UserRecord reference doc for the contents of userRecord.
    System.out.println("Successfully fetched user data: " + userRecord.getUid());
    // [END get_user_by_id]
  }

  public static void getUserByEmail(String email) throws InterruptedException, ExecutionException {
    // [START get_user_by_email]
    UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmailAsync(email).get();
    // See the UserRecord reference doc for the contents of userRecord.
    System.out.println("Successfully fetched user data: " + userRecord.getEmail());
    // [END get_user_by_email]
  }

  public static void getUserByPhoneNumber(
      String phoneNumber) throws InterruptedException, ExecutionException {
    // [START get_user_by_phone]
    UserRecord userRecord = FirebaseAuth.getInstance().getUserByPhoneNumberAsync(phoneNumber).get();
    // See the UserRecord reference doc for the contents of userRecord.
    System.out.println("Successfully fetched user data: " + userRecord.getPhoneNumber());
    // [END get_user_by_phone]
  }

  public static void createUser() throws InterruptedException, ExecutionException {
    // [START create_user]
    CreateRequest request = new CreateRequest()
        .setEmail("user@example.com")
        .setEmailVerified(false)
        .setPassword("secretPassword")
        .setPhoneNumber("+11234567890")
        .setDisplayName("John Doe")
        .setPhotoUrl("http://www.example.com/12345678/photo.png")
        .setDisabled(false);

    UserRecord userRecord = FirebaseAuth.getInstance().createUserAsync(request).get();
    System.out.println("Successfully created new user: " + userRecord.getUid());
    // [END create_user]
  }

  public static void createUserWithUid() throws InterruptedException, ExecutionException {
    // [START create_user_with_uid]
    CreateRequest request = new CreateRequest()
        .setUid("some-uid")
        .setEmail("user@example.com")
        .setPhoneNumber("+11234567890");

    UserRecord userRecord = FirebaseAuth.getInstance().createUserAsync(request).get();
    System.out.println("Successfully created new user: " + userRecord.getUid());
    // [END create_user_with_uid]
  }

  public static void updateUser(String uid) throws InterruptedException, ExecutionException {
    // [START update_user]
    UpdateRequest request = new UpdateRequest(uid)
        .setEmail("user@example.com")
        .setPhoneNumber("+11234567890")
        .setEmailVerified(true)
        .setPassword("newPassword")
        .setDisplayName("Jane Doe")
        .setPhotoUrl("http://www.example.com/12345678/photo.png")
        .setDisabled(true);

    UserRecord userRecord = FirebaseAuth.getInstance().updateUserAsync(request).get();
    System.out.println("Successfully updated user: " + userRecord.getUid());
    // [END update_user]
  }

  public static void setCustomUserClaims(
      String uid) throws InterruptedException, ExecutionException {
    // [START set_custom_user_claims]
    // Set admin privilege on the user corresponding to uid.
    Map<String, Object> claims = new HashMap<>();
    claims.put("admin", true);
    FirebaseAuth.getInstance().setCustomUserClaimsAsync(uid, claims).get();
    // The new custom claims will propagate to the user's ID token the
    // next time a new one is issued.
    // [END set_custom_user_claims]

    String idToken = "id_token";
    // [START verify_custom_claims]
    // Verify the ID token first.
    FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdTokenAsync(idToken).get();
    if (Boolean.TRUE.equals(decoded.getClaims().get("admin"))) {
      // Allow access to requested admin resource.
    }
    // [END verify_custom_claims]

    // [START read_custom_user_claims]
    // Lookup the user associated with the specified uid.
    UserRecord user = FirebaseAuth.getInstance().getUserAsync(uid).get();
    System.out.println(user.getCustomClaims().get("admin"));
    // [END read_custom_user_claims]
  }

  public static void setCustomUserClaimsScript() throws InterruptedException, ExecutionException {
    // [START set_custom_user_claims_script]
    UserRecord user = FirebaseAuth.getInstance()
        .getUserByEmailAsync("user@admin.example.com").get();
    // Confirm user is verified.
    if (user.isEmailVerified()) {
      Map<String, Object> claims = new HashMap<>();
      claims.put("admin", true);
      FirebaseAuth.getInstance().setCustomUserClaimsAsync(user.getUid(), claims).get();
    }
    // [END set_custom_user_claims_script]
  }

  public static void setCustomUserClaimsInc() throws InterruptedException, ExecutionException {
    // [START set_custom_user_claims_incremental]
    UserRecord user = FirebaseAuth.getInstance()
        .getUserByEmailAsync("user@admin.example.com").get();
    // Add incremental custom claim without overwriting the existing claims.
    Map<String, Object> currentClaims = user.getCustomClaims();
    if (Boolean.TRUE.equals(currentClaims.get("admin"))) {
      // Add level.
      currentClaims.put("level", 10);
      // Add custom claims for additional privileges.
      FirebaseAuth.getInstance().setCustomUserClaimsAsync(user.getUid(), currentClaims).get();
    }
    // [END set_custom_user_claims_incremental]
  }

  public static void listAllUsers() throws InterruptedException, ExecutionException  {
    // [START list_all_users]
    // Start listing users from the beginning, 1000 at a time.
    ListUsersPage page = FirebaseAuth.getInstance().listUsersAsync(null).get();
    while (page != null) {
      for (ExportedUserRecord user : page.getValues()) {
        System.out.println("User: " + user.getUid());
      }
      page = page.getNextPage();
    }

    // Iterate through all users. This will still retrieve users in batches,
    // buffering no more than 1000 users in memory at a time.
    page = FirebaseAuth.getInstance().listUsersAsync(null).get();
    for (ExportedUserRecord user : page.iterateAll()) {
      System.out.println("User: " + user.getUid());
    }
    // [END list_all_users]
  }

  public static void deleteUser(String uid) throws InterruptedException, ExecutionException {
    // [START delete_user]
    FirebaseAuth.getInstance().deleteUserAsync(uid).get();
    System.out.println("Successfully deleted user.");
    // [END delete_user]
  }

  public static void createCustomToken() throws InterruptedException, ExecutionException {
    // [START custom_token]
    String uid = "some-uid";

    String customToken = FirebaseAuth.getInstance().createCustomTokenAsync(uid).get();
    // Send token back to client
    // [END custom_token]
    System.out.println("Created custom token: " + customToken);
  }

  public static void createCustomTokenWithClaims() throws InterruptedException, ExecutionException {
    // [START custom_token_with_claims]
    String uid = "some-uid";
    Map<String, Object> additionalClaims = new HashMap<String, Object>();
    additionalClaims.put("premiumAccount", true);

    String customToken = FirebaseAuth.getInstance()
        .createCustomTokenAsync(uid, additionalClaims).get();
    // Send token back to client
    // [END custom_token_with_claims]
    System.out.println("Created custom token: " + customToken);
  }

  public static void verifyIdToken(String idToken) throws InterruptedException, ExecutionException {
    // [START verify_id_token]
    // idToken comes from the client app (shown above)
    FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdTokenAsync(idToken).get();
    String uid = decodedToken.getUid();
    // [END verify_id_token]
    System.out.println("Decoded ID token from user: " + uid);
  }

  public static void verifyIdTokenCheckRevoked(String idToken) throws InterruptedException, ExecutionException {
    // [START verify_id_token_check_revoked]
    try {
      // Verify the ID token while checking if the token is revoked by passing checkRevoked
      // as true.
      boolean checkRevoked = true;
      FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdTokenAsync(idToken, checkRevoked).get();
      String uid = decodedToken.getUid();
      System.out.println("Decoded ID token not revoked from user: " + uid);
    } 
    catch (FirebaseAuthException e) {
      if ("id-token-revoked" ==  e.getErrorCode()) {
        // Token is valid but has been revoked.
        // When this occurs, inform the user to reauthenticate or signOut() the user.
      } else {
        // Error is other than "revoked" token is invalid.
      }
    }
    // [END verify_id_token_check_revoked]
  }

  public static void revokeIdTokens(String idToken) throws InterruptedException, ExecutionException { 
    String uid="someUid";
    // [START revoke_tokens]
    FirebaseToken decodedToken = FirebaseAuth.getInstance().revokeRefreshTokens(uid).get();
    UserRecord user = FirebaseAuth.getInstance().getUserAsync(uid).get();
    // Convert to seconds as the auth_time in the token claims is in seconds too. 
    long revocationSecond = user.getTokensValidAfterTimestamp() / 1000;
    System.err.println("Tokens revoked at: ", revocationSecond);
    // [END revoke_tokens]

    // [START save_revocation_in_db]
    UserRecord user = FirebaseAuth.getInstance().getUserAsync(uid).get();
    // Convert to seconds as the auth_time in the token claims is in seconds too. 
    long revocationSecond = user.getTokensValidAfterTimestamp() / 1000;
    // Save the refresh token revocation timestamp. This is needed to track ID token
    // revocation via Firebase rules.
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("metadata/" + uid);
    ref.setValueAsync(MapBuilder.of("revokeTime", revocationSecond)).get();
    // [END save_revocation_in_db]
    
  }

  public static void main(String[] args) throws InterruptedException, ExecutionException {
    System.out.println("Hello, AuthSnippets!");

    // Initialize Firebase
    try {
      // [START initialize]
      FileInputStream serviceAccount = new FileInputStream("service-account.json");
      FirebaseOptions options = new FirebaseOptions.Builder()
          .setCredentials(GoogleCredentials.fromStream(serviceAccount))
          .build();
      FirebaseApp.initializeApp(options);
      // [END initialize]
    } catch (IOException e) {
      System.out.println("ERROR: invalid service account credentials. See README.");
      System.out.println(e.getMessage());

      System.exit(1);
    }

    // Smoke test
    createUserWithUid();
    getUserById("some-uid");
    getUserByEmail("user@example.com");
    getUserByPhoneNumber("+11234567890");
    updateUser("some-uid");
    //setCustomUserClaims("some-uid");
    listAllUsers();
    deleteUser("some-uid");
    createCustomToken();
    createCustomTokenWithClaims();
    System.out.println("Done!");
  }

}
