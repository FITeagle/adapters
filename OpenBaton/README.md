Effects of SFA-Methods on the OpenBaton-Adapter:

GetVersion:
  GetVersion has no effect on the OpenBaton-Adapter.
  It is giving back informations about the Testbed.
  (Like Location of the Testbed, SFA version etc)
ListResources:
  ListResources is generally giving back all Information about the Adapters that are deployed on the Testbed
  and which Resources they are able to deploy/start/create.
  While deploying the Adapter he is checking for VNFs that are available at the OpenBaton-Server or which are safed in the Database.
  Also the Adapter is checking the Database for already existing Resources that were created with an older Adapter before.
Allocate:
   Allocate is reserving a Slot in FitEagle/Testbed for the User with the given Resources.
   So if you configure the OpenBaton-Adapter to maximal deploy 3 NetworkServiceRecords it will reserve you 
   one Slot if there is one free. Else you will get a Deny back.
  This call has no effect on the OpenBatonAdapter. It is handled in FitEagle it self.
Provision:
  The Provision call is handled by the Adapter and creates a NetworkServiceDescriptor Object to whom all
  VirtualNetworkFunctions are added. After this is finished it is pushed to the OpenBatonServer, where it 
  actually gets created and started.
  As a Response the Adapter is getting a NetworkServiceRecord which he is cheking (calling OpenBaton-Server and update Informations) 
  each 30 Seconds if it is finally started.
  When the NetworkServiceRecord is started it is changing this Value in the Database and the next time someone is calling a
  Status on the Resource he is getting this Information.
Renew:
  The Renew-call is renewing the Slice and the Lifeduration of it. If this is not called on time the Slice is deleted.
  It has no effect on the Adapter because he this will be handled by FitEagle itself.
Status:
  When you call the Status-call on your Resource you get some Informations back.
  In case of the OpenBaton-Adapter you get all importend Informations back that are saved in the Database,
  but the Adapter itself makes no calls to the OpenBaton-Server or other Machines.
