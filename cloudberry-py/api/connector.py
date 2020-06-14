import requests


class CloudberryConnector:
    cb_url = None
    cb_port = None

    def connect(self, url, port):
        self.cb_url = url
        self.cb_port = port

    def create_connection_url(self):
        return str(self.cb_url) + ':' + str(self.cb_port)

    def upload(self, file_name):
        files = {'file': open(file_name, 'rb')}
        url = self.create_connection_url() + '/logs/upload'
        r = requests.post(url, files=files)
        return r.json()

    def gat_names(self):
        url = self.create_connection_url() + '/logs/all'
        r = requests.get(url)
        return r.json()


cbc = CloudberryConnector()
cbc.connect("http://localhost", 9000)
resp = cbc.upload('emas-20190412T120536.log')
print(resp)
