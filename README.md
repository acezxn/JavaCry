# JavaCry Java Ransomware Framework
**Warning: this project is for educational purpose only. Please make sure that the program encrypts the right directory. I do not take any responsibility of any damage.**

![image](https://github.com/acezxn/JavaCry/blob/main/images/JRFUI.png)

![image](https://github.com/acezxn/JavaCry/blob/main/images/YourFilesHaveBeenEncrypted.png)

### What is it?

It is a project to build the java ransomwares generator and manage ransomware victims. It generates customized ransomwares, listens for victim's private key, and handles decryption requests. 

## User guideline
* To run the JRF framework, move to JRF/Classes directory and run `java JRF [commands]` in terminal
* To run MainServer, which handles keys and decryption requests, move to JRF/Classes directory and run `java MainServer` in terminal

### Parameters:
* Target path: the path which the ransomware encrypts the files. Make sure it is **set to the right folder**. Leaving blank would mean that the ransomware would encrypt its local directory.
* Local IP: the attackers IP address
* Reverse shell: 
    * port: any port except 5555 and 6666, since these are the ports used by KeyServer and DecryptRequestHandler
    * persistence: Decryptor tries to configure the victim system so that the attacker's access would be persistent. This may be detected as some system has permission setting blocking this utility.

### Command line arguments:

```
 -a,--Address <Address>              Crypto Address
 -c,--Cost <Cost in ETH>             Cost of decryption in ETH. The
                                     default is 1 ETH.
 -h,--IP <IP>                        Local IP address
 -i,--Interactive                    Interactive mode
 -o,--Export <Export path>           Destination for export
 -p,--revPort <port>                 Reverse shell port
 -P,--Persistence                    Activate reverse shell persistence
 -r,--UseRev                         Activate reverse shell
 -t,--TargetDir <Target directory>   Target directory for the ransomware
                                     to encrypt
 -u,--UI                             GUI mode
```
#### Note: reverse shell and persistence are optional


## Features:

### JavaCry Functionality:
* It is the ransomware delivered to the victim.
* Creates a random number, as the victim’s identity.
* Hashes the number and sends it to the server.
* Receives its public key from the server for decryption.
* Creates 256 bit AES key, use the key to encrypt recursively in the target folder.
* Save key: encrypt the AES key with the public key, and save as Key_protected.key.
* Creates, compiles, and executes Decryptior.jar.
* Decryptior.jar:
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
            * show: show information of victims
            * remove <idx>: remove information about the specific victim in keys.csv. This would cause the victim to be unable to make decryption requests. 
            * off: turn of server, rejecting all requests
            * on: turn on server
            * reset: reset keys.csv
    * DecryptRequestHandler
		* Commands:
			* help: show help page
			* show: show authenticated clients
			* reject <idx>: reject a specific request
			* accept <idx>: accept a specific request
			* off: turn of server, rejecting all requests
			* on: turn on server

### Extra spice:
* integrated reverse shell in Decryptor, and run it everytime the victim runs Decryptor
* persistence trials can be enabled, protecting the attacker's access (Note: it may be detected due to some security policy of computers)

## How it works
### Encryption
![image](https://github.com/acezxn/JavaCry/blob/main/images/JavaCry_Encryption.jpg)
### Decryption
![image](https://github.com/acezxn/JavaCry/blob/main/images/JavaCry_Decryption.jpg)