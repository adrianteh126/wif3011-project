'use client'

import { useState } from 'react'
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

interface Result {
  data: {
    [key: string]: number
  }
  elapsed_time: number
}

export default function Home() {
  const { toast } = useToast()

  const [selectedFile, setSelectedFile] = useState(null)
  const [isLoading, setIsLoading] = useState(false)
  const [data, setData] = useState<Result>()

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
      const response = await fetch('http://localhost:4000/api/upload', {
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
      toast({ title: '❌ Failed to upload file', description: String(error) })
    }
    setIsLoading(false)
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
            <CardContent>
              <div className="grid w-full max-w-sm items-center gap-1.5">
                <Label htmlFor="text-file">Text File</Label>
                <Input id="text-file" type="file" onChange={handleFileChange} />
              </div>
            </CardContent>
            <CardFooter>
              <Button type="submit">
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
                      <p>
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
