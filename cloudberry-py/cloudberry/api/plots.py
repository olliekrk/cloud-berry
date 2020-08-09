class Plots:

    # todo: better if it took cloudberry.DataSeries as an argument
    @staticmethod
    def comparison_plot(data_frames: list,
                        y_field: str,
                        x_field='_time',
                        groupby='series_name',
                        figsize=(15, 5)):
        merged_df = DataHelpers.merge_data_frames(data_frames)
        fig, ax = plt.subplots(figsize=figsize)
        for key, grp in merged_df.groupby([groupby]):
            ax.plot(grp[x_field], grp[y_field], label=key)
        ax.legend()
