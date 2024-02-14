from functools import wraps
from hashlib import sha256
from flask import Blueprint, request, Response, jsonify

v2_blueprint = Blueprint('v2', __name__, url_prefix="/v2")

store = {}
MAX_STORE_SIZE = 100


def authenticate(username, password):
    hashed_password = sha256(password.encode()).hexdigest()
    # Use an uncrackable password so that only automated services can log in
    return username == 'operator' and hashed_password == 'e49bfb73b105d24f0ac6a5e827b2990b3191f9797f09860bf3b788763a433777'


def requires_auth(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        auth = request.authorization
        if not auth or not authenticate(auth.username, auth.password):
            return Response('Could not verify your credentials', 401, {'WWW-Authenticate': 'Basic realm="Login Required"'})
        return f(*args, **kwargs)
    return decorated


@v2_blueprint.route('/store', methods=['GET'])
@requires_auth
def get_store():
    return jsonify(store)


@v2_blueprint.route('/store/<key>', methods=['GET'])
@requires_auth
def get_value(key):
    if key in store:
        return jsonify({key: store[key]})
    else:
        return jsonify({'error': 'Key not found'}), 404


@v2_blueprint.route('/store', methods=['POST'])
@requires_auth
def add_to_store():
    data = request.get_json()
    if 'key' not in data or 'value' not in data:
        return jsonify({'error': 'Key or value not provided'}), 400
    if len(store) >= MAX_STORE_SIZE:
        return jsonify({'error': 'Store already full, delete some entries first'}), 409
    key = data['key']
    value = data['value']
    if key in store:
        return jsonify({'error': "Key already exists"}), 403
    store[key] = value
    return jsonify({'success': True})


@v2_blueprint.route('/store/<key>', methods=['PUT'])
@requires_auth
def update_value(key):
    if key == "FLAG":
        return jsonify({'error': "Cannot modify FLAG"}), 403
    data = request.get_json()
    if key not in store:
        return jsonify({'error': 'Key not found'}), 404
    store[key] = data['value']
    return jsonify({'success': True})


@v2_blueprint.route('/store/<key>', methods=['DELETE'])
@requires_auth
def delete_value(key):
    if key == "FLAG":
        return jsonify({'error': "Cannot delete FLAG"}), 403
    if key in store:
        del store[key]
        return jsonify({'success': True})
    else:
        return jsonify({'error': 'Key not found'}), 404
