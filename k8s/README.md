Run this for GKE
`k apply -f . -f gke`

Run this for local machine
`k apply -f . -f local`

The below files (found in `local`) were NOT written by me but necessary for HPA metrics
https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
https://github.com/kedacore/keda/releases/download/v2.17.1/keda-2.17.1.yaml
