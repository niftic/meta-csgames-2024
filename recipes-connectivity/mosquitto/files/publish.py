import paho.mqtt.publish as publish
import subprocess

with open("flag.txt", "r") as f:
    flag = f.read().rstrip("\n")

temperature = subprocess.run(["/usr/bin/vcgencmd", "measure_temp"], stdout=subprocess.PIPE, stderr=subprocess.STDOUT).stdout.decode().rstrip("\n").split("=")[1]

publish.single(topic="temperature",
                payload=f"CPU temperature: {temperature}",
                auth={"username": "narcissus", "password": "narcissus"})

publish.single(topic="s3cr3t_t0p1c",
                payload=f"Energy level: {flag}",
                auth={"username": "narcissus", "password": "narcissus"})
