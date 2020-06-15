from api import CloudberryConfig, LogsApi

config = CloudberryConfig('http://localhost', 9000)
logs_api = LogsApi(config)

evaluation_id = input('Evaluation ID: ')
workplace_id = input('Workplace ID: ')

logs_query = logs_api \
    .workplace() \
    .by_evaluation_id(evaluation_id) \
    .by_workplace_id(workplace_id)

logs = logs_query.get_raw()
logs_df = logs_query.get_dataframe()
logs_csv = logs_query.get_csv()

print(logs)
