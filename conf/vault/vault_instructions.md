1. Enable ‘app-id’ Vault auth backend:
	vault auth-enable app-id
2. Add ‘app-id’ mapping with MAC address values for all worker nodes:
	vault write auth/app-id/map/app-id/<mac_address_worker#> value=root display_name=worker#

3. Add ‘user-id’ mapping for all modules using play generated crypto key:
	vault write auth/app-id/map/user-id/<play.crypto.secret configuration value> value=<mac_address_worker1>,<mac_address_worker2>...<mac_address_worker#>
