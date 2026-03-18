*plan d'action de la realisation de la partie 2 du projet.*

**modification**
  - renommer des constantes liée à la problematique denommage 
          -- renommage de contante useragent dans 
  -  creation de variable pour supprimer des nombres magiques 
         -- IoBuffer buf = IoBuffer.allocate(102);  DANS LA METGHODE endOnPlayStatus DE PLAYENGINED ON AVAIT LA TAILLE INITUIALE DU BUFFER QUI N'ETAIT PAS STOCKER DANS UNE VARIABLE 

  - suppresiion des if inutiles 
  - SUPPRESSION DE CODES MOR 
               - if statement have empty body dans common/src/main/java/org/red5/server/stream/VideoFrameDropper.java
  - SUPPRESSION D'UN CONCATENATION AVEC "" DANS src/main/java/org/red5/server/ContextLoader.java String configReplaced = config + "";
  - reduire la complexite cyclomatique de playengine
  - reduire la complexite cognitive de de play engine 
  - reduire le nombre de ligne duppliquees    common/src/mmain/java/org/red5/server/adapter/application.java   common/src/mmain/java/org/red5/server/stream/consummer/selectfileconsuer
  - ajout d'un test pertinent 
  - remplacer le fait qu'une methode retourne un code d'erreur par le fait qu'eel leve une exception 
  - decomposer RTMPconnection + reduire sa complexite 
  - ajout d'un super classe pour supprilmer des methodes duppliquees 
  - fusion de classes
  - supprimer des dcycles dans la dependeance entre les paquets
  - suppression des paxkages ne contenat pas de classes 
  -   cc


                                                                                                                                                                                                                                                                                           