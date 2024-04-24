const baseURL = "http://localhost:4000"

const constantURLList = {
    sequential: `${baseURL}/api/sequential`,
    concurrentJavaStream: `${baseURL}/api/concurrent/java-stream`,
    concurrentForkJoin: `${baseURL}/api/concurrent/fork-join`,
    comparison: `${baseURL}/api/comparison`
}

export default constantURLList;