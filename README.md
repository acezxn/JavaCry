# JavaCry
Warning: this project is only for educational purpose only. I do not take any responsibility of any damage.
### the project is still in development

It is a project to build a ransomware made with java


## JavaCry Functionality:
* Creates a random number, as the victim’s identity
* Hashes the number and sends it to the server.
* Receives its public key from the server for decryption.
* Creates 256 bit AES key, use the key to encrypt recursively in the target folder
* Save key: encrypt the AES key with the public key, and save as Key_protected.key
* Creates, compiles, and executes decryption.java
* decryptor.java:
** UI for decryption
** Button to request for decryption (enable only when payment succeeded)
** Wait for acceptance
** Decrypt files:
** Receives the private key
** Read Key_protected.key
** Decrypt Key_protected.key with the private key, get the AES key
** Use the AES key to recursively decrypt the target folder
** Delete itself, Key_protected.key
## MainServer Functionality:
Commands:
clear: clear the screen
help: show help page
KS header: run KeyServer commands
DR header: run DecryptRequestHandler commands
KeyServer:
For every client instance:
Creates a key pair, keep the private key
Receives the client’s id hash
Send public key to the client
Store the client’s IP, private key, and id hash in keys.csv
Commands:
help: show help page
off: turn of server, rejecting all requests
on: turn on server
reset: reset keys.csv
manual: show manual page
## DecryptRequestHandler
Commands:
help: show help page
show: show authenticated clients
reject <idx>: reject a specific request
accept <idx> accept a specific request
off: turn of server, rejecting all requests
on: turn on server
manual: show manual page
