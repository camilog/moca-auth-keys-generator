# Authority Keys Generator (moca-auth-keys-generator)
First part of the [*MoCa QR*](http://mocaqr.niclabs.cl) Voting System project.

Generates the public key to encrypt the ballots, and all the shares on which the private key is separated, to distribute among all the authorities.

## Files
1. **crypto.GenerateKeys.java**: Main class of the program, where are all the logic and the methods for the generation, uploading and saving of the public and private keys.

2. **GUIJavaFX.java**: Class that manages the JavaFX GUI environment. This environment also needs the presence of the following files: mainWindow.fxml, configWindow.fxml, generationWindow.fxml, gui.MainWindowController.java, ConfigWindowController.java, gui.GenerationWindowController.java, javaFx.css and background.jpg.

3. **objects.AuthorityPublicKeyResponse.java**: Class for the creation of the Authority Public Key object after the retrieving of the JSON from the Bulletin Board server.

4. **objects.AuthorityPublicKeyValueResponse.java**: Class to manage one of the values that comes in the response of the BB for the Authority Public Key.

5. **objects.RevisionNumber.java**: Class to manage the revision number, this is necessary in order to delete the previous authority public key stored in the BB.

6. **objects.PrivateKey.java**: Class to serialize the private key of the authorities and deliver to them in a JSON stored in a file.

## External Libraries
1. **[Paillier Threshol Encryption ToolBox](http://cs.utdallas.edu/dspl/cgi-bin/pailliertoolbox/index.php?go=home)**: Implementation of a Threshold variant of the Paillier encryption scheme.
2. **[Gson](https://github.com/google/gson)**: Java library to convert Java Object to their JSON representation and viceversa. 

## How to Use
* Download the .jar file [here](https://github.com/CamiloG/moca_qr/blob/master/KeyGeneration_Apps/AuthKeysGenerator_light.jar?raw=true).
* Put the file authKeyGenerator.jar in the project folder.
* Execute authKeyGenerator.jar with `$ java -jar authKeyGenerator.jar`

### Configuration
* First of all you have to configure the root address for the Bulletin Board server. Select 'Configure Bulletin Board address'.
* The address is now shown on the top box of the main window.

### Key Generation Process
* Select 'Generate Keys'.
* First, the program asks how many authorities will be on the election and how many of those authorities will be necessary to decrypt the final result (threshold scheme).
* Select a path where to store the public key generated (to deliver to the applications that don't connect to the Bulletin Board server).
* The program uploads the public key to the Bulletin Board server.
* Now, select the folder where to save each of the shares of the private key that are being generated. The idea here is to save each share of the private key on a different external storage to distribute among the authorities.
* After saving all the shares, the program finishes.
