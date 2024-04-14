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

interface Result {
  data: {
    [key: string]: number
  }
  elapsed_time: number
}

type Algorithm =
  | 'sequential'
  | 'concurrent-java-stream'
  | 'concurrent-fork-join'

export default function Home() {
  const baseUrl = 'http://localhost:4000'
  const { toast } = useToast()

  const [selectedFile, setSelectedFile] = useState(null)
  const [isLoading, setIsLoading] = useState(false)
  const [data, setData] = useState<Result>()
  const [selectOption, setSelectedOption] = useState<Algorithm>('sequential')

  const handleFileChange = (event: any) => {
    setSelectedFile(event.target.files[0])
  }

  const handleSubmit = async (event: any) => {
    setIsLoading(true)
    event.preventDefault()

    if (!selectedFile) {
      toast({ title: '💡 Please select a file' })
      return setIsLoading(false)
    }

    const formData = new FormData()
    formData.append('file', selectedFile)

    try {
      let fetchUrl

      switch (selectOption) {
        case 'sequential':
          fetchUrl = `${baseUrl}/api/sequential`
          break
        case 'concurrent-java-stream':
          fetchUrl = `${baseUrl}/api/concurrent/java-stream`
          break
        case 'concurrent-fork-join':
          fetchUrl = `${baseUrl}/api/concurrent/fork-join`
          break
      }

      const response = await fetch(fetchUrl, {
        method: 'POST',
        body: formData
      })

      const data = await response.json()

      if (response.ok) {
        toast({ title: '✅ File uploaded successfully' })
        setData(data)
      } else {
        toast({ title: '❌ Failed to upload file', description: data.error })
      }
    } catch (error) {
      console.error('Error uploading file:', error)
      toast({
        title: '❌ Failed to upload file',
        description: String(error)
      })
    }
    setIsLoading(false)
  }

  const handleOptionChange = (value: Algorithm) => {
    setSelectedOption(value)
  }

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
            </CardContent>
            <CardFooter>
              <Button type="submit" disabled={isLoading}>
                Upload
                {isLoading && (
                  <div className="ps-3">
                    <LineMdLoadingTwotoneLoop />
                  </div>
                )}
              </Button>
            </CardFooter>
          </form>
        </Card>
        <br />
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
            </div>
          ) : (
            <div>
              <p>Upload a txt file to see the result</p>
            </div>
          )}
        </div>
      </div>
    </main>
  )
}
