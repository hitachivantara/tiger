## API test 1
#### APIs that will be executed:
 * Login
 * Upload file for new asset avatar type
 * Create two new asset avatar types: Toyota Electric Forklift and Toyota Electric Stacker
 * Create two new asset avatars, with the type created previously
#### Steps:
 * Download the "lib" directory and place it in to your local file system. For example, /home/pentaho/lib 
 * Download two image files to your home directory.
 * In your home directory, run the client to test APIs
      java -cp .:./lib/* org.pentaho.tiger.lumada.ApiTest --host 10.0.2.15 --username YOUR_USERNAME --password YOUR_PASSWORD
#### Login to Lumada console to verify      
