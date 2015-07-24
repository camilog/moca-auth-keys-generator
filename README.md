# AuthKeyGenerator
First part of the project [*Voter-Ballot Self Verification*](https://github.com/CamiloG/VoterBallotSelfVerificationSystem).

Generates the public key to encrypt the ballots, and all the shares on which the private key is separated, to distribute among all the authorities.

## Files
1. **GenerateKeys_CORE.java**: 
2. **GenerateKeys_light.java**:
3. **GenerateKeys_swing.java**:
4. **AuthorityPublicKey.java**:

## How to Use
* Download the .jar file [here](https://github.com/CamiloG/VoterBallotSelfVerificationSystem/blob/master/KeyGeneration_Apps/AuthKeysGenerator_light.jar?raw=true).
* Put the file authKeyGenerator.jar in the project folder.
* Execute authKeyGenerator.jar with `$ java -jar authKeyGenerator.jar`
* First, the program asks how many authorities will be on the election.
* Next, the program asks how many of those authorities will be necessary to decrypt the final result.
* The program uploads the public key to the Bulletin Board server.
* The program will start to ask the folder where to save all the shares of the private key that will be generated. The idea here is to save each share of the private key on a different external storage.
* After saving all the shares, the program finishes.