from api import CloudberryConfig, FileUploader

config = CloudberryConfig('http://localhost', 9000)
uploader = FileUploader(config)
response = uploader.upload_file('emas-20190412T120536.log')
print(response)
