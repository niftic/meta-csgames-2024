# Chlorophyllai Secret Server Code

from chlorophyllai_core import encrypted_data_handler

class SecretServer:
    def __init__(self):
        self.encrypted_data = None
        self.server_key = None

    def initialize_server(self, key):
        self.server_key = key
        self.encrypted_data = encrypted_data_handler.encrypt("TopSecretManifest2024")

    def retrieve_manifest(self, user_key):
        if user_key == self.server_key:
            return encrypted_data_handler.decrypt(self.encrypted_data)
        else:
            return "Access Denied: Incorrect Key"

    def update_manifest(self, new_manifest, admin_key):
        if admin_key == self.server_key:
            self.encrypted_data = encrypted_data_handler.encrypt(new_manifest)
            return "Manifest Updated Successfully"
        else:
            return "Access Denied: Incorrect Admin Key"

secret_server = SecretServer()
secret_server.initialize_server("SuperSecretKey123")
