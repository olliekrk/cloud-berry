import json


class JSONUtil:
    @staticmethod
    def multipart_payload(payload):
        return None, json.dumps(payload), 'application/json'
