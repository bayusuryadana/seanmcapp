import { Button, Modal, Box, Typography, Alert, TextField, MenuItem, Select, Grid, InputLabel, FormControlLabel, Checkbox } from "@mui/material";
import axios from "axios";
import { useContext, useState, FormEvent, useEffect } from "react";
import { UserContext, UserContextType } from "./UserContext";
import { WalletDetail } from "./model";
import { modalStyle } from "./constant";

interface WalletModalProps {
  onClose: () => void
  date: string
  onSuccess: (row: WalletDetail, actionText: String|undefined) => void
  walletDetail: WalletDetail|null
}

export const WalletModal = (props: WalletModalProps) => {
  const { userContext } = useContext(UserContext) as UserContextType;
  const [alert, setAlert] = useState({display: 'none', text: ''})
  const [data, setData] = useState<WalletDetail|null>(null)

  useEffect( () => {
    setData(props.walletDetail)
  }, [props.walletDetail])

  const getAccount = (currency: string): string => {
    if (currency === 'SGD') 
      return 'DBS'
    if (currency === 'IDR')
      return 'BCA'
    return ''
  }

  const getActionText = () => {
    const detail = props.walletDetail ?? {} as WalletDetail
    if (detail.id === -1) {
      return 'Create'
    } else if (detail.id > -1 &&
      detail.date && detail.name && detail.category && detail.currency && detail.amount && detail.done !== null && detail.account) {
      return 'Edit'
    } else if (detail.id > -1) {
      return 'Delete'
    }
  }
  const actionText = getActionText()

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    const data = new FormData(event.currentTarget)
    if (actionText === 'Create') {
      submitCreate(data)
    } else if (actionText === 'Edit') {
      submitEdit(data)
    } else if (actionText === 'Delete') {
      submitDelete()
    }
  }

  const submitCreate = (data: FormData) => {
    const currency = data.get('currency')?.toString() ?? ""
    const done = data.get('done')?.toString() ? true : false
    const payload = {
      // 'id': parseInt(props.walletDetail?.id.toString() ?? ""),
      'date': parseInt(props.date),
      'name': data.get('name')?.toString() ?? "",
      'amount': parseInt(data.get('amount')?.toString() ?? ""),
      'category': data.get('category')?.toString() ?? "",
      'currency': currency,
      'account': getAccount(currency),
      'done': done
    }

    axios.post('api/wallet/create', payload, {
      auth: {
        username: 'bayu',
        password: userContext ?? ""
      }
    }).then((response) => {
      setAlert({display: 'none', text: ''})
      const newData = {...payload, id: response.data.data.id}
      props.onSuccess(newData, actionText)
    })
    .catch((error) => {
      console.log(error)
      setAlert({display: 'true', text: 'Gagal tot!'})
    })
  }

  const submitEdit = (data: FormData) => {
    const currency = data.get('currency')?.toString() ?? ""
    const done = data.get('done')?.toString() ? true : false
    const payload = {
      'id': parseInt(props.walletDetail?.id.toString() ?? ""),
      'date': parseInt(props.date),
      'name': data.get('name')?.toString() ?? "",
      'amount': parseInt(data.get('amount')?.toString() ?? ""),
      'category': data.get('category')?.toString() ?? "",
      'currency': currency,
      'account': getAccount(currency),
      'done': done
    }

    axios.post('api/wallet/update', payload, {
      auth: {
        username: 'bayu',
        password: userContext ?? ""
      }
    }).then((_) => {
      setAlert({display: 'none', text: ''})
      props.onSuccess(payload, actionText)
    })
    .catch((error) => {
      console.log(error)
      setAlert({display: 'true', text: 'Gagal tot!'})
    })
  }

  const submitDelete = () => {
    const id = parseInt(props.walletDetail?.id.toString() ?? '-1')
    axios.post('api/wallet/delete', {id: id}, {
      auth: {
        username: 'bayu',
        password: userContext ?? ""
      }
    }).then((response) => {
      if (response.data.data == '1') {
        props.onSuccess({id: id} as WalletDetail, actionText)
      } else {
        const errorMessage = 'something is wrong with the API'
        console.log(errorMessage)
        setAlert({display: 'true', text: errorMessage})
      }
    })
    .catch((error) => {
      console.log(error)
      setAlert({display: 'true', text: 'Failed to delete!'})
    })
  }

  const renderForm = () => {
    return (
      <>
        <Alert id="wrong-password-alert" severity="error" sx={{display: alert.display, mb: 1}}>{alert.text}</Alert>
        <Grid container spacing={1}>
          <Grid item xs={12}>
            <InputLabel>Name</InputLabel>
            <TextField required fullWidth name="name" type="text" value={data?.name ?? ''} variant="standard" onChange={(event) => {setData({...data, name: event.target.value} as WalletDetail)}} />
          </Grid>
          <Grid item xs={12}>
            <InputLabel>Amount</InputLabel>
            <TextField required fullWidth name="amount" type="number" value={data?.amount ?? ''} variant="standard"  onChange={(event) => {setData({...data, amount: parseInt(event.target.value)} as WalletDetail)}} />
          </Grid>
          <Grid item xs={12}>
            <InputLabel>Category</InputLabel>
            <Select
              required
              fullWidth
              value={data?.category ?? ""}
              label="Category"
              name="category"
              variant="standard"
              onChange={(event) => setData({...data, category: event.target.value} as WalletDetail)}
            >
              <MenuItem value='Bonus'>Bonus</MenuItem>
              <MenuItem value='Daily'>Daily</MenuItem>
              <MenuItem value='Fashion'>Fashion</MenuItem>
              <MenuItem value='Funding'>Funding</MenuItem>
              <MenuItem value='IT Stuff'>IT Stuff</MenuItem>
              <MenuItem value='Misc'>Misc</MenuItem>
              <MenuItem value='ROI'>ROI</MenuItem>
              <MenuItem value='Rent'>Rent</MenuItem>
              <MenuItem value='Salary'>Salary</MenuItem>
              <MenuItem value='Temp'>Temp</MenuItem>
              <MenuItem value='Transfer'>Transfer</MenuItem>
              <MenuItem value='Travel'>Travel</MenuItem>
              <MenuItem value='Wellness'>Wellness</MenuItem>
              <MenuItem value='Zakat'>Zakat</MenuItem>
            </Select>
          </Grid>
          <Grid item xs={6}>
            <InputLabel>Currency</InputLabel>
            <Select
              required
              fullWidth
              value={data?.currency ?? ""}
              label="currency"
              name="currency"
              variant="standard"
              onChange={(event) => {
                const curr = event.target.value
                setData({...data, currency: event.target.value, account: getAccount(curr).toString()} as WalletDetail)
              }}
            >
              <MenuItem value={'SGD'}>SGD</MenuItem>
              <MenuItem value={'IDR'}>IDR</MenuItem>
            </Select>
          </Grid>
          <Grid item xs={6}>
            <InputLabel>Account</InputLabel>
            <TextField required disabled fullWidth value={data?.account} name="account" type="text" variant="standard"/>
          </Grid>
          <Grid item xs={12}>
            <FormControlLabel
              control={
                <Checkbox 
                  color="secondary" 
                  name="done" 
                  value={data?.done ? 'yes' : ''} 
                  checked={data?.done??false} 
                  onChange={(event) => {
                    setData({...data, done: event.target.checked} as WalletDetail)
                  }} />
              }
              label="Is it done?"
            />
          </Grid>
        </Grid>
      </>
    )
  }

  return (
    <Modal
      open={props.walletDetail !== null}
      onClose={props.onClose}
      aria-labelledby="modal-modal-title"
      aria-describedby="modal-modal-description"
    >
      <Box sx={modalStyle}>
        <Typography id="modal-modal-title" variant="h6" component="h2">
          {actionText}
        </Typography>
        <Typography id="modal-modal-description" sx={{ mt: 2 }}>
          <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
            {actionText === 'Delete' || renderForm()}
            <Button type="submit" fullWidth variant="contained" sx={{ mt: 3, mb: 2 }}>
              {actionText === 'Delete' ? 'Delete' : 'Submit'}
            </Button>
          </Box>
        </Typography>
      </Box>
    </Modal>
  );
}
