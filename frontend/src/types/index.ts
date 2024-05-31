export interface Result {
  data: {
    [key: string]: number;
  };
  elapsed_time: number;
  algorithm_processing_time: number;
  file_processing_time: number;
}

export interface ComparisonResult {
  sequential: {
    [key: string]: number;
  };
  javaStream: {
    [key: string]: number;
  };
  forkJoin: {
    [key: string]: number;
  };
}

export type Algorithm = 'sequential' | 'concurrent-java-stream' | 'concurrent-fork-join';

export type Comparison = {
  labels: string[];
  datasets: (
    | {
        label: string;
        data: number[];
        fill: boolean;
        backgroundColor: string;
        borderColor: string;
      }
    | {
        label: string;
        data: number[];
        fill: boolean;
        borderColor: string;
        backgroundColor?: undefined;
      }
  )[];
};

export type WordCloud = {
  text: string;
  value: number;
};
