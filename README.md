# AuthKeyGenerator
First part of the [*MoCa QR*](http://mocaqr.niclabs.cl) Voting System project.

Generates the public key to encrypt the ballots, and all the shares on which the private key is separated, to distribute among all the authorities.

## Files
1. **GenerateKeys.java**: Main class of the program, where are all the logic and the methods to the generation, uploading and saving of the public and private keys.

2. **GUILanterna.java**: Class that manages the Lanterna GUI environment, made to run on console-text-only devices (Raspberry PI for example).

3. **GUISwing.java**: Class that manages the Java-Swing GUI environment, for all the devices that can run a graphics interface.

4. **AuthorityPublicKeyResponse.java**: Class for the creation of the object after the retrieving of the JSON from the Bulletin Board server.

## How to Use
* Download the .jar file [here](https://github.com/CamiloG/moca_qr/blob/master/KeyGeneration_Apps/AuthKeysGenerator_light.jar?raw=true).
* Put the file authKeyGenerator.jar in the project folder.
* Execute authKeyGenerator.jar with `$ java -jar authKeyGenerator.jar`

### Configuration
* First of all you have to configure the root address for the Bulletin Board server. Select 'Configure Bulletin Board address'.
* The address is now shown on the top box of the window.

### Key Generation Process
* Select 'Generate Keys'.
* First, the program asks how many authorities will be on the election.
* Next, the program asks how many of those authorities will be necessary to decrypt the final result.
* Select a path where to store the public key generated (to deliver to the applications that don't connect to the Bulletin Board server).
* The program uploads the public key to the Bulletin Board server.
* Now, select the folder where to save each of the shares of the private key that are being generated. The idea here is to save each share of the private key on a different external storage to distribute among the authorities.
* After saving all the shares, the program finishes.
