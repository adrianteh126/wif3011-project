'use client'

import React, { useEffect, useState } from 'react'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle
} from '@/components/ui/card'
import { useToast } from '@/components/ui/use-toast'
import { LineMdLoadingTwotoneLoop } from '@/components/icon/LineMdLoadingTwotoneLoop'
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group'
import { Switch } from '@/components/ui/switch'
import { Slider } from '@/components/ui/slider'
import { LineChart } from '@/components/ui/line-chart'
import WordCloudComponent from '@/components/ui/word-cloud'
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger
} from '@/components/ui/tooltip'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'

import {
  Result,
  ComparisonResult,
  Algorithm,
  Comparison,
  WordCloud
} from '@/types'

export default function Home() {
  const baseUrl = 'http://localhost:4000'
  const { toast } = useToast()

  // UI state
  const [selectedFile, setSelectedFile] = useState(null)
  const [isLoading, setIsLoading] = useState(false)
  const [isCompare, setIsCompare] = useState(false)
  const [isComparisonLoading, setIsComparisonLoading] = useState(false)

  // Data state
  const [data, setData] = useState<Result>()
  const [comparisonData, setComparisonData] = useState<ComparisonResult>(
    {} as ComparisonResult
  )
  const [comparison, setComparison] = useState<Comparison>({} as Comparison)
  const [wordCloudData, setWordCloudData] = useState<WordCloud[]>([])

  // Query parameters
  const [selectOption, setSelectedOption] = useState<Algorithm>('sequential')
  const [isAscending, setIsAscending] = useState(false)
  const [numOfWords, setNumOfWords] = useState(50)

  // Handle file change event
  const handleFileChange = (event: any) => {
    if (event.target.files) {
      setSelectedFile(event.target.files[0])
    }
  }

  // Handle form submission
  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault()
    setIsCompare(false)
    setIsLoading(true)

    if (!selectedFile) {
      toast({ title: '💡 Please select a file' })
      setIsLoading(false)
      return
    }

    const formData = new FormData()
    formData.append('file', selectedFile)

    try {
      const fetchUrl = `${baseUrl}/api/${selectOption}?numOfWords=${numOfWords}&sortAscending=${isAscending}`
      const response = await fetch(fetchUrl, {
        method: 'POST',
        body: formData
      })

      const result = await response.json()

      if (response.ok) {
        toast({ title: '✅ File uploaded successfully' })
        setData(result)
      } else {
        toast({ title: '❌ Failed to upload file', description: result.error })
      }
    } catch (error) {
      console.error('Error uploading file:', error)
      toast({ title: '❌ Failed to upload file', description: String(error) })
    } finally {
      setIsLoading(false)
    }
  }

  // Handle option change event
  const handleOptionChange = (value: Algorithm) => {
    setSelectedOption(value)
  }

  // Handle sort change event
  const handleSortChange = (value: boolean) => {
    setIsAscending(value)
  }

  // Handle number of words change event
  const handleNumOfWordsChange = (value: number[]) => {
    setNumOfWords(value[0])
  }

  // Handle comparison algorithm
  const handleCompareAlgo = async () => {
    if (!selectedFile) {
      toast({ title: '💡 Please select a file' })
      return setIsLoading(false)
    }

    const formData = new FormData()
    formData.append('file', selectedFile)
    setIsComparisonLoading(true)

    try {
      const fetchUrl = `${baseUrl}/api/comparison`
      const response = await fetch(fetchUrl, {
        method: 'POST',
        body: formData
      })

      const result = await response.json()

      if (response.ok) {
        toast({ title: '✅ File uploaded successfully' })
        setComparisonData(result)
      } else {
        toast({ title: '❌ Failed to upload file', description: result.error })
      }
    } catch (error) {
      console.error('Error uploading file:', error)
      toast({ title: '❌ Failed to upload file', description: String(error) })
    } finally {
      setIsComparisonLoading(false)
    }
  }

  // Update comparison data on change
  useEffect(() => {
    if (!Object.keys(comparisonData).length) return

    const lineData: Comparison = {
      labels: ['T1', 'T2', 'T3'],
      datasets: []
    }

    const labels = Object.keys(comparisonData)
    const colors = ['#10439F', '#874CCC', '#C65BCF']

    ;[
      comparisonData.sequential,
      comparisonData.javaStream,
      comparisonData.forkJoin
    ].forEach((data, i) => {
      lineData.datasets.push({
        label: labels[i],
        data: Object.values(data),
        fill: false,
        borderColor: colors[i]
      })
    })

    setComparison(lineData)
    setIsCompare(true)
  }, [comparisonData])

  // Update word cloud data on data change
  useEffect(() => {
    const wordCloudWords = Object.entries(data?.data || {}).map(
      ([text, value]) => ({ text, value })
    )
    setWordCloudData(wordCloudWords)
  }, [data])

  return (
    <main className="container flex flex-col justify-center items-center min-h-dvh">
      <div className="w-2/3 py-8">
        {/* Title */}
        <div className="text-4xl font-bold">Bag-of-Words Generator</div>
        <br />

        {/* Card */}
        <Card>
          <CardHeader>
            <CardTitle>Upload File</CardTitle>
            <CardDescription>
              Upload your .txt file here. Not exceeding 200mb.
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
                    value="concurrent/java-stream"
                    id="option-concurrent"
                  />
                  <Label htmlFor="option-concurrent">
                    Concurrent / JavaStream
                  </Label>
                </div>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem
                    value="concurrent/fork-join"
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
                onClick={handleCompareAlgo}
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

        {/* Result */}
        {!isCompare ? (
          <div>
            {data ? (
              <div className="flex flex-col">
                <div>
                  <TooltipProvider>
                    <Tooltip>
                      <TooltipTrigger>
                        <p>
                          Elapsed Time: <strong> {data.elapsed_time} ms</strong>
                        </p>
                      </TooltipTrigger>
                      <TooltipContent>
                        <p>
                          File Processing Time:
                          <strong> {data.file_processing_time} ms</strong>
                        </p>
                        <p>
                          Algorithm Processing Time:
                          <strong> {data.algorithm_processing_time} ms</strong>
                        </p>
                      </TooltipContent>
                    </Tooltip>
                  </TooltipProvider>
                </div>
                <div>
                  <Tabs defaultValue="wordCloud">
                    <div className="flex justify-end">
                      <TabsList>
                        <TabsTrigger value="wordCloud">Word Cloud</TabsTrigger>
                        <TabsTrigger value="list">List</TabsTrigger>
                      </TabsList>
                    </div>
                    <TabsContent value="wordCloud">
                      <WordCloudComponent words={wordCloudData} />
                    </TabsContent>
                    <TabsContent value="list">
                      <p>Data:</p>
                      <div className="ps-6 grid grid-cols-5">
                        {Object.entries(data.data).map(([key, value]) => (
                          <div key={key}>
                            <p className="truncate">
                              &quot;<strong>{key}</strong>&quot;: {value}
                            </p>
                          </div>
                        ))}
                      </div>
                    </TabsContent>
                  </Tabs>
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
  )
}
