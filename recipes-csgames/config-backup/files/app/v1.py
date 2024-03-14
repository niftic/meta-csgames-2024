from flask import Blueprint, request, jsonify, send_file
from Crypto.Cipher import AES
from Crypto.Random import get_random_bytes
import hashlib
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.asymmetric import padding
from cryptography.hazmat.primitives.serialization import load_pem_public_key
from cryptography.exceptions import InvalidSignature
import json
import socket
from io import BytesIO

v1_blueprint = Blueprint('v1', __name__, url_prefix='/v1')

with open('public_key.pem', 'rb') as f:
    public_key = load_pem_public_key(f.read())

def generate_encryption_key():
    key = hashlib.sha256(socket.gethostname().encode() + b'BackUp05@').digest()
    return key

def encrypt_data(data, key):
    iv = get_random_bytes(16)
    cipher = AES.new(key, AES.MODE_GCM, nonce=iv)
    ciphertext, tag = cipher.encrypt_and_digest(data.encode())
    return iv + ciphertext + tag

def decrypt_data(data, key):
    iv = data[:16]
    tag = data[-16:]
    ciphertext = data[16:-16]
    cipher = AES.new(key, AES.MODE_GCM, nonce=iv)
    plaintext = cipher.decrypt_and_verify(ciphertext, tag)
    return plaintext.decode()

def verify_signature(data, signature):
    try:
        public_key.verify(
            signature,
            data.encode(),
            padding.PSS(
                mgf=padding.MGF1(hashes.SHA256()),
                salt_length=padding.PSS.MAX_LENGTH
            ),
            hashes.SHA256()
        )
        return True
    except InvalidSignature:
        return False

@v1_blueprint.route('/backup', methods=['GET'])
def backup():
    try:
        with open('config.json', 'r') as file:
            config_data = json.load(file)
        with open('flag.txt', 'r') as flag:
            config_data['FLAG'] = flag.read().rstrip('\n')

        key = generate_encryption_key()
        encrypted_file = BytesIO(encrypt_data(json.dumps(config_data, indent=2), key))

        return send_file(encrypted_file, mimetype="application/json", as_attachment=True, download_name="config.json")
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@v1_blueprint.route('/restore', methods=['POST'])
def restore():
    try:
        signature = request.form.get('signature')
        if not signature:
            return jsonify({'error': 'Signature not provided in the request parameters'}), 400

        key = generate_encryption_key()

        if 'file' not in request.files:
            return jsonify({'error': 'No file part in the request'}), 400

        uploaded_file = request.files['file']
        if uploaded_file.filename == '':
            return jsonify({'error': 'No selected file'}), 400

        uploaded_data = uploaded_file.read()
        if not verify_signature(uploaded_data, signature):
            return jsonify({'error': 'Signature verification failed'}), 401

        decrypted_data = decrypt_data(uploaded_data, key)

        with open('config.json', 'w') as file:
            file.write(decrypted_data)

        return jsonify({'message': 'Config file restored successfully'})
    except Exception as e:
        return jsonify({'error': str(e)}), 500
