import api

config = api.CloudberryConfig('http://localhost', 9000)
logs_api = api.LogsApi(config)
flux_api = api.FluxApi(config)

evaluation_id = input('Evaluation ID: ')
workplace_id = input('Workplace ID: ')

logs_query = logs_api \
    .workplace() \
    .by_evaluation_id(evaluation_id) \
    .by_workplace_id(workplace_id)

logs = logs_query.get_raw()
logs_df = logs_query.get_dataframe()
logs_csv = logs_query.get_csv()
print(logs, logs_df)

while True:
    raw_query = input('Provide Flux query (or type \'exit\' to quit):\n')
    if raw_query == 'exit':
        break
    else:
        result = flux_api.query(raw_query)
        print(result)
