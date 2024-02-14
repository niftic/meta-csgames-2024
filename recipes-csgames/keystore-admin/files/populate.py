#!/usr/bin/python3
import os
import grp
import requests

if not os.path.exists("/var/log/keystore/keystore.log"):
    with open("flag.txt", "r") as f:
        flag = f.read()
    requests.post("http://localhost:6039/v2/store", json={'key': 'FLAG', 'value': flag}, auth=("operator", "3c69feff40c1eadef48c7439d49ad95032194131ee50ef588e639d1088f8b924"))

    os.makedirs("/var/log/keystore", exist_ok=True)
    with open("/var/log/keystore/keystore.log", "w") as logfile:
        logfile.write(f"[*] Connected to http://operator:3c69feff40c1eadef48c7439d49ad95032194131ee50ef588e639d1088f8b924@localhost:6039/v2/store\n")
        logfile.write(f"[*] Successfully added flag to the store\n")

    # Set permissions
    gid = grp.getgrnam("adm").gr_gid
    os.chmod("/var/log/keystore/", 0o0550)
    os.chmod("/var/log/keystore/keystore.log", 0o0640)
    os.chown("/var/log/keystore/", 0, gid)
    os.chown("/var/log/keystore/keystore.log", 0, gid)

