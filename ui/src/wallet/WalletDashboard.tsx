import { Container, Alert, Grid, Paper, Typography } from "@mui/material"
import Chart from "./Chart"
import { Detail } from "./Detail"
import { Title } from "./Title"
import { WalletPlanned, WalletDetail, WalletDashboardData, WalletAlert } from "./model"
import { WalletModal } from "./Modal"
import axios from "axios"
import { useContext, useEffect, useState } from "react"
import { UserContext, UserContextType } from "./UserContext"

export const WalletDashboard = () => {

  const { userContext } = useContext(UserContext) as UserContextType
  const [alert, setAlert] = useState<WalletAlert>({display: 'none', text: ''})
  const [data, setData] = useState<WalletDashboardData|null>(null);
  const [walletDetail, setWalletDetail] = useState<WalletDetail|null>(null)
  const [date, setDate] = useState('')

  const onSuccess = (row: WalletDetail, actionText: String|undefined) => {
    setWalletDetail(null)
    if (data !== null) {
      if (actionText === 'Create') {
        const updatedDetail = {...data, detail: [...data.detail, row]}
        setData(updatedDetail)
      } else if (actionText === 'Edit') {
        const index = data?.detail.findIndex((d) => d.id === row.id) ?? -1
        if (index && index > -1 && data) {
          const updatedDetail = {...data, detail: [...data.detail.filter((_, i) => i !== index), row]}
          setData(updatedDetail)
        }
      } else if (actionText === 'Delete') {
        const index = data?.detail.findIndex((d) => d.id === row.id) ?? -1
        if (index && index > -1 && data) {
          setData({...data, detail: data.detail.filter((_, i) => i !== index)})
        }
      }
    }
  }

  const getWalletDashboard = (dateParam: string) => {
    axios.get('http://localhost:8080/api/wallet/dashboard', {
      headers: {
        Authorization: 'Bearer ' + (userContext ?? "")
      },
      params: {
        date: dateParam
      },
    })
    .then((response) => {
      setAlert({display: 'none', text: ''})
      setData(response.data)
      setDate(dateParam)
    })
    .catch((error) => {
      console.log(error)
      setAlert({display: 'true', text: 'Data failed to fetch/parse!'})
    })
  }

  useEffect(() => {
    const newDate = new Date()
    const dateString = newDate.getFullYear().toString() + ('0' + (newDate.getMonth() + 1).toString()).slice(-2)
    setDate(dateString)
    getWalletDashboard(dateString)
  }, [])

  return (
    <>
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Alert id="invalid-data-alert" severity="error" sx={{ mb: 2, display: alert.display}}>{alert.text}</Alert>
        <Grid container spacing={3}>
          {/* Balance */}
          <Grid item xs={12} md={8} lg={9}>
            <Paper sx={{p: 2, display: 'flex', flexDirection: 'column', height: 240, }}>
                <Chart data={data?.chart.balance ?? []} />
            </Paper>
          </Grid>
          {/* Saving accounts */}
          <Grid item xs={12} md={4} lg={3}>
            <Paper sx={{p: 2, display: 'flex', flexDirection: 'column', height: 240, }}>
              <Title>Current Savings</Title>
              <Typography color="text.secondary">
                on DBS account
              </Typography>
              <Typography component="p" variant="h5" sx={{ flex: 0.5 }}>
                S$ {data?.savings.dbs.toLocaleString()}
              </Typography>
              <Typography color="text.secondary">
                on BCA account
              </Typography>
              <Typography component="p" variant="h5">
                Rp. {data?.savings.bca.toLocaleString()}
              </Typography>
            </Paper>
          </Grid>
          {/* Data */}
          <Grid item xs={12}>
            <Paper sx={{ p: 2, display: 'flex', flexDirection: 'column' }}>
              <Detail 
                date={date}
                rows={data?.detail ?? []} 
                planned={data?.planned ?? { sgd: 0, idr: 0} as WalletPlanned}
                updateDashboard={getWalletDashboard}
                createHandler={() => {setWalletDetail({ id: -1 } as WalletDetail)}}
                editHandler={(walletDetail: WalletDetail) => {setWalletDetail(walletDetail)}} 
                deleteHandler={(id: Number) => {setWalletDetail({ id: id} as WalletDetail)}} 
              />
            </Paper>
          </Grid>
        </Grid>
      </Container>
      
      <WalletModal 
          onClose={() => setWalletDetail(null)}
          date={date}
          onSuccess={onSuccess}
          walletDetail={walletDetail}
          />
    </>
  )
}