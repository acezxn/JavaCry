# JavaCry
**Warning: this project is for educational purpose only. Please make sure that the program encrypts the right directory. I do not take any responsibility of any damage.**

![image](https://github.com/acezxn/JavaCry/blob/main/images/YourFilesHaveBeenEncrypted.png)

### the project is still in development

It is a project to build a ransomware made with java.


## User guideline
* To compile JavaCry.java, move to Payload and run `javac JavaCry.java -d Classes --release 8` on terminal
* To package JavaCry into a Jar file, move to Payload/Classes/ and run `jar -cvmf ../manifest.txt JavaCry.jar *.class` on terminal
* Set the remote host IP address of JavaCry and Decryptor.java appropriately to your server's IP address.
* Make sure targetPath variable, which is in JavaCry.java, is **set to the right folder**.
* The base64 encoded payload embedded in JavaCry resembles Decryptor.java. Modify Decryptor.java as needed and encode the new Base64 encoded string to JavaCry.

## Features:

### JavaCry Functionality:
* It is the ransomware delivered to the victim.
* Creates a random number, as the victim’s identity.
* Hashes the number and sends it to the server.
* Receives its public key from the server for decryption.
* Creates 256 bit AES key, use the key to encrypt recursively in the target folder.
* Save key: encrypt the AES key with the public key, and save as Key_protected.key.
* Creates, compiles, and executes decryption.java.
* decryptor.java:
    * Opens a UI for decryption guideline
    * Shows a a button for sending decryption requests
    * Wait for the acceptance of DecryptRequestHandler
    * Decrypt files:
	    * Receives the private key
	    * Read Key_protected.key
	    * Decrypt Key_protected.key with the private key, get the AES key
	    * Use the AES key to recursively decrypt the target folder
    * Delete itself, Key_protected.key
 
### MainServer Functionality:
* It is the server that manages the victim's private keys and decryption requests.
* Commands:
    * clear: clear the screen
    * help: show help page
    * KS header: run KeyServer commands
    * DR header: run DecryptRequestHandler commands
    * KeyServer:
        * The server creates RSA key pairs for every victim, sending the public keys to the victim to encrypt the AES key used for file encryption.
        * For every client instance:
            * Creates a key pair, keep the private key
            * Receives the client’s id hash
            * Send public key to the client
            * Store the client’s IP, private key, and id hash in keys.csv
        * Commands:
            * help: show help page
            * off: turn of server, rejecting all requests
            * on: turn on server
            * reset: reset keys.csv
    * DecryptRequestHandler
		* Commands:
			* help: show help page
			* show: show authenticated clients
			* reject <idx>: reject a specific request
			* accept <idx> accept a specific request
			* off: turn of server, rejecting all requests
			* on: turn on server

## How it works
### Encryption
![image](https://github.com/acezxn/JavaCry/blob/main/images/JavaCry_Encryption.jpg)
### Decryption
![image](https://github.com/acezxn/JavaCry/blob/main/images/JavaCry_Decryption.jpg)