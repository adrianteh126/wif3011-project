"use client";

import React, { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { useToast } from "@/components/ui/use-toast";
import { LineMdLoadingTwotoneLoop } from "@/components/icon/LineMdLoadingTwotoneLoop";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Switch } from "@/components/ui/switch";
import { Slider } from "@/components/ui/slider";
import { LineChart } from "@/components/ui/line-chart";
import WordCloudComponent from "@/components/ui/word-cloud";
import { ChartOptions } from "chart.js";

interface Result {
  data: {
    [key: string]: number;
  };
  elapsed_time: number;
}

interface ComparisonResult {
  sequential: {
    [key: string]: number;
  }
  javaStream: {
    [key: string]: number;
  }
  forkJoin: {
    [key: string]: number;
  }
}

type Algorithm =
  | "sequential"
  | "concurrent-java-stream"
  | "concurrent-fork-join";

type Comparison = {
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

type WordCloud = {
  text: string;
  value: number;
};

export default function Home() {
  const baseUrl = "http://localhost:4000";
  const { toast } = useToast();
  // ui
  const [selectedFile, setSelectedFile] = useState(null); // user select file
  const [isLoading, setIsLoading] = useState(false); // handleSubmit loading indicator
  // fetch api
  const [data, setData] = useState<Result>(); // data fetched from request
  const [selectOption, setSelectedOption] = useState<Algorithm>("sequential"); // fetch api select options
  // query params: result query
  const [isAscending, setIsAscending] = useState(false); // sorting, default: descending
  const [numOfWords, setNumOfWords] = useState(50); // return numOfWords, default: 50

  const [isCompare, setIsCompare] = useState(false);
  const [comparison, setComparison] = useState<Comparison>({} as Comparison);
  const [isComparisonLoading, setIsComparisonLoading] = useState(false); // handleComparison loading indicator
  const [comparisonData, setComparisonData] = useState<ComparisonResult>({} as ComparisonResult)

  const [wordCloudData, setWordCloudData] = useState<WordCloud[]>([]);

  const handleFileChange = (event: any) => {
    setSelectedFile(event.target.files[0]);
  };

  const handleSubmit = async (event: any) => {
    setIsCompare(false);
    setIsLoading(true);
    event.preventDefault();

    if (!selectedFile) {
      toast({ title: "💡 Please select a file" });
      return setIsLoading(false);
    }

    // request body: upload file
    const formData = new FormData();
    formData.append("file", selectedFile);

    try {
      let fetchUrl;

      switch (selectOption) {
        case "sequential":
          fetchUrl = `${baseUrl}/api/sequential?numOfWords=${numOfWords}&sortAscending=${isAscending}`;
          break;
        case "concurrent-java-stream":
          fetchUrl = `${baseUrl}/api/concurrent/java-stream?numOfWords=${numOfWords}&sortAscending=${isAscending}`;
          break;
        case "concurrent-fork-join":
          fetchUrl = `${baseUrl}/api/concurrent/fork-join?numOfWords=${numOfWords}&sortAscending=${isAscending}`;
          break;
      }

      const response = await fetch(fetchUrl, {
        method: "POST",
        body: formData,
      });

      const data = await response.json();

      if (response.ok) {
        toast({ title: "✅ File uploaded successfully" });
        setData(data);
      } else {
        toast({ title: "❌ Failed to upload file", description: data.error });
      }
    } catch (error) {
      console.error("Error uploading file:", error);
      toast({
        title: "❌ Failed to upload file",
        description: String(error),
      });
    }
    setIsLoading(false);
  };

  const handleOptionChange = (value: Algorithm) => {
    setSelectedOption(value);
  };

  const handleSortChange = (value: boolean) => {
    setIsAscending(value);
  };

  const handleNumOfWordsChange = (value: number[]) => {
    setNumOfWords(value[0]);
  };

  const compareAlgo = async () => {
    if (!selectedFile) {
      toast({ title: "💡 Please select a file" });
      return setIsLoading(false);
    }

    // request body: upload file
    const formData = new FormData();
    formData.append("file", selectedFile);
    setIsComparisonLoading(true)

    try {
      let fetchUrl = `${baseUrl}/api/comparison`;
      const response = await fetch(fetchUrl, {
        method: "POST",
        body: formData,
      });
      const data = await response.json();

      if (response.ok) {
        toast({ title: "✅ File uploaded successfully" });
        setComparisonData(data)
      } else {
        toast({ title: "❌ Failed to upload file", description: data.error });
      }
    } catch (error) {
      console.error("Error uploading file:", error);
      toast({
        title: "❌ Failed to upload file",
        description: String(error),
      });
    }
  };

  useEffect(() => {
    if(!Object.keys(comparisonData).length){
      return
    }
    const lineData : Comparison = {
      labels: ["T1", "T2", "T3"],
      datasets: [],
    };
    const labels = Object.keys(comparisonData)
    const sequentialData = Object.values(comparisonData.sequential);
    const javaStreamData = Object.values(comparisonData.javaStream);
    const forkJoinData = Object.values(comparisonData.forkJoin);
    let finalData : number[] = [];
    const colors = ["#10439F", "#874CCC", "C65BCF"];
    finalData = finalData.concat(sequentialData, javaStreamData, forkJoinData)
    for(let i = 0; i < 3; i++){
      lineData.datasets.push({
        label: labels[i],
        data: [finalData[i], finalData[i + 1], finalData[i + 2]],
        fill: true,
        borderColor: colors[i],
        backgroundColor: colors[i],
      })
    }
    setIsComparisonLoading(false);
    setIsCompare(true)
    setComparison(lineData);
  }, [comparisonData])

  useEffect(() => {
    const wordCloudWords = Object.entries(data?.data || {})
                                 .map(([text, value]) => ({text, value}));
    setWordCloudData(wordCloudWords);
  }, [data]);

  return (
    <main className="container flex flex-col justify-center items-center min-h-dvh">
      <div className="w-2/3 py-8">
        <div className="text-4xl font-bold">Bag-of-Words Generator</div>
        <br />
        <Card>
          <CardHeader>
            <CardTitle>Upload File</CardTitle>
            <CardDescription>
              Upload your .txt file here. Not exceeding 10mb.
            </CardDescription>
          </CardHeader>
          <form onSubmit={handleSubmit}>
            <CardContent className="flex flex-col gap-6">
              <div className="grid w-full max-w-sm items-center gap-1.5">
                <Label htmlFor="text-file">Text File</Label>
                <Input id="text-file" type="file" onChange={handleFileChange} />
              </div>
              <RadioGroup
                defaultValue="sequential"
                onValueChange={handleOptionChange}
              >
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="sequential" id="option-sequential" />
                  <Label htmlFor="option-sequential">Sequential</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem
                    value="concurrent-java-stream"
                    id="option-concurrent"
                  />
                  <Label htmlFor="option-concurrent">
                    Concurrent / JavaStream
                  </Label>
                </div>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem
                    value="concurrent-fork-join"
                    id="option-concurrent-2"
                  />
                  <Label htmlFor="option-concurrent-2">
                    Concurrent / ForkJoin
                  </Label>
                </div>
              </RadioGroup>
              <div className="flex items-center space-x-2">
                <Switch id="sort-switch" onCheckedChange={handleSortChange} />
                <Label htmlFor="sort-switch">Sort Ascending</Label>
              </div>
              <div className="flex flex-col gap-3">
                <Label htmlFor="slider">Number of Words: {numOfWords}</Label>
                <Slider
                  id="slider"
                  className="w-[30%]"
                  defaultValue={[50]}
                  max={100}
                  step={1}
                  onValueChange={handleNumOfWordsChange}
                />
              </div>
            </CardContent>
            <CardFooter className="flex gap-5">
              <Button type="submit" disabled={isLoading} className="p-5">
                Upload
                {isLoading && (
                  <div className="ps-3">
                    <LineMdLoadingTwotoneLoop />
                  </div>
                )}
              </Button>
              <Button
                type="button"
                disabled={isLoading}
                onClick={() => {
                  compareAlgo()
                }}
              >
                Compare
                {isComparisonLoading && (
                  <div className="ps-3">
                    <LineMdLoadingTwotoneLoop />
                  </div>
                )}
              </Button>
            </CardFooter>
          </form>
        </Card>
        <br />
        {!isCompare ? (
          <div>
            {data ? (
              <div className="flex flex-col ">
                <div>
                  <p>
                    Elapsed Time: <strong> {data.elapsed_time} ms</strong>
                  </p>
                </div>
                <div>
                  <p>Data: </p>
                  <div className="ps-6 grid grid-cols-5">
                    {Object.entries(data.data).map(([key, value]) => (
                      <div key={key}>
                        <p className="truncate">
                          &quot;<strong>{key}</strong>&quot;: {value}
                        </p>
                      </div>
                    ))}
                  </div>
                </div>
                <div>
                  <WordCloudComponent words={wordCloudData} />
                </div>
              </div>
            ) : (
              <div>
                <p>Upload a txt file to see the result</p>
              </div>
            )}
          </div>
        ) : (
          <div className="p-10">
            <LineChart data={comparison} />
          </div>
        )}
      </div>
    </main>
  );
}
