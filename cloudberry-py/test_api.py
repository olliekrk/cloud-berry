from api import CloudberryConfig, LogsApi

config = CloudberryConfig('http://localhost', 9000)
logs_api = LogsApi(config)

logs_query = logs_api \
    .workplace() \
    .by_evaluation_id('00b570fe-cd0f-4038-ab2d-080d407e1c64') \
    .by_workplace_id(1)

logs = logs_query.get_raw()
logs_df = logs_query.get_dataframe()
logs_csv = logs_query.get_csv()

print(logs)
