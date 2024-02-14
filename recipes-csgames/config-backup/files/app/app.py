from flask import Flask
from v1 import v1_blueprint
from v2 import v2_blueprint

app = Flask(__name__)
app.register_blueprint(v1_blueprint)
app.register_blueprint(v2_blueprint)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=6039)
